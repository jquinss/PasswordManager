package com.jquinss.passwordmanager.util.misc;

import com.jquinss.passwordmanager.exceptions.LoadKeyPairException;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

public class CryptoUtils {
    public static class AsymmetricCrypto {
        private final Cipher encryptionCipher;
        private final Cipher decryptionCipher;
        public AsymmetricCrypto(String algorithm, KeyPair keypair) throws NoSuchPaddingException, NoSuchAlgorithmException,
                InvalidKeyException {
            encryptionCipher = initializeCipher(algorithm,Cipher.ENCRYPT_MODE, keypair.getPublic());
            decryptionCipher = initializeCipher(algorithm, Cipher.DECRYPT_MODE, keypair.getPrivate());
        }

        public String encrypt(String text) throws IllegalBlockSizeException, BadPaddingException {
            return CryptoUtils.encrypt(text, encryptionCipher);
        }

        public String decrypt(String text) throws IllegalBlockSizeException, BadPaddingException {
            return CryptoUtils.decrypt(text, decryptionCipher);
        }
    }
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

    public static SecretKey getSecretKeyFromPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
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

    public static byte[] encrypt(byte[] input, String algorithm, Key key, IvParameterSpec iv)
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

    public static byte[] encrypt(byte[] input, String algorithm, Key key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = initializeCipher(algorithm, Cipher.ENCRYPT_MODE, key);
        return encrypt(input, cipher);
    }

    private static String encrypt(String input, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    private static byte[] encrypt(byte[] input, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(input);
    }

    public static String decrypt(String cipherText, String algorithm, Key key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = initializeCipher(algorithm, Cipher.DECRYPT_MODE, key, iv);
        return decrypt(cipherText, cipher);
    }

    public static byte[] decrypt(byte[] cipherText, String algorithm, Key key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = initializeCipher(algorithm, Cipher.DECRYPT_MODE, key, iv);
        return decrypt(cipherText, cipher);
    }

    public static String decrypt(String cipherText, String algorithm, Key key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = initializeCipher(algorithm, Cipher.DECRYPT_MODE, key);
        return decrypt(cipherText, cipher);
    }

    public static byte[] decrypt(byte[] cipherText, String algorithm, Key key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = initializeCipher(algorithm, Cipher.DECRYPT_MODE, key);
        return decrypt(cipherText, cipher);
    }

    private static String decrypt(String cipherText, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }

    private static byte[] decrypt(byte[] cipherText, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(cipherText);
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

    public static KeyPair  loadKeyPairFromPEMFile(String publicKeyPath, String privateKeyPath) throws LoadKeyPairException {
        try (FileReader publicKeyFileReader = new FileReader(publicKeyPath);
             FileReader privateKeyFileReader = new FileReader(privateKeyPath);
             PemReader publicPEMReader = new PemReader(publicKeyFileReader);
             PemReader privatePemReader = new PemReader(privateKeyFileReader)) {

            PemObject publicPemObject = publicPEMReader.readPemObject();
            PemObject privatePemObject = privatePemReader.readPemObject();
            byte[] publicContent = publicPemObject.getContent();
            byte[] privateContent = privatePemObject.getContent();


            return loadKeyPair(publicContent, privateContent, "RSA");
        }
        catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            throw new LoadKeyPairException("Error loading key-pair from file");
        }
    }

    public static KeyPair loadKeyPair(byte[] publicKey, byte[] privateKey, String algorithm) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey);

        return new KeyPair(keyFactory.generatePublic(publicKeySpec), keyFactory.generatePrivate(privateKeySpec));
    }

    public static boolean isValidKeyPair(KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException,
            SignatureException {
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        byte[] challenge = new byte[10000];
        ThreadLocalRandom.current().nextBytes(challenge);

        // sign using the private key
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(challenge);
        byte[] signature = sig.sign();

        //verify the signature using the public key
        sig.initVerify(publicKey);
        sig.update(challenge);

        return sig.verify(signature);
    }
}