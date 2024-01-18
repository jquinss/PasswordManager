package com.jquinss.passwordmanager.util.misc;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CryptoUtils {
    private CryptoUtils() {}
    public static byte[] generateSaltBytes(int numBytes) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[numBytes];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public static byte[] getHashFromString(String text, int hashLength) {
        Argon2Parameters.Builder builder = createArgon2ParametersBuilder();
        return generateHash(text, hashLength, builder);
    }

    public static byte[] getHashFromString(String text, int hashLength, byte[] salt) {
        Argon2Parameters.Builder builder = createArgon2ParametersBuilder(salt);
        return generateHash(text, hashLength, builder);
    }

    private static byte[] generateHash(String text, int hashLength, Argon2Parameters.Builder argon2ParametersBuilder) {
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(argon2ParametersBuilder.build());
        byte[] result = new byte[hashLength];
        generator.generateBytes(text.getBytes(StandardCharsets.UTF_8), result, 0, result.length);
        return result;
    }

    private static Argon2Parameters.Builder createArgon2ParametersBuilder() {
        return new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(1)
                .withMemoryAsKB(66536)
                .withParallelism(1);
    }

    private static Argon2Parameters.Builder createArgon2ParametersBuilder(byte[] salt) {
        return createArgon2ParametersBuilder().withSalt(salt);
    }

    public static SecretKey getKeyFromPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(keySpec).getEncoded(), "AES");
    }

    public static IvParameterSpec getIvParameterSpec(int numBytes) {
        byte[] iv = new byte[numBytes];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String encrypt(String input, String algorithm, Key key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = initializeCipher(algorithm, Cipher.ENCRYPT_MODE, key, iv);
        return encrypt(input, cipher);
    }

    public static String encrypt(String input, String algorithm, Key key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = initializeCipher(algorithm, Cipher.ENCRYPT_MODE, key);
        return encrypt(input, cipher);
    }

    private static String encrypt(String input, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String cipherText, String algorithm, Key key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = initializeCipher(algorithm, Cipher.DECRYPT_MODE, key, iv);
        return decrypt(cipher, cipherText);
    }

    public static String decrypt(String cipherText, String algorithm, Key key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = initializeCipher(algorithm, Cipher.DECRYPT_MODE, key);
        return decrypt(cipher, cipherText);
    }

    private static String decrypt(Cipher cipher, String cipherText) throws IllegalBlockSizeException, BadPaddingException {
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }

    private static Cipher initializeCipher(String algorithm, int opMode, Key key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(opMode, key, iv);
        return cipher;
    }

    private static Cipher initializeCipher(String algorithm, int opMode, Key key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(opMode, key);
        return cipher;
    }

    public static KeyPair generateKeyPair(String algorithm, int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }
}