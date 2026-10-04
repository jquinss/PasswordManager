package com.jquinss.passwordmanager.crypto;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.security.SecureRandom;

public final class Argon2Kdf {
    private static final SecureRandom RANDOM = new SecureRandom();
    private Argon2Kdf(){}

    public static byte[] generateSalt(int numBytes) {
        byte[] salt = new byte[numBytes];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public static byte[] deriveMasterKey(char[] password, Argon2Parameters parameters, int outputLength) {
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(parameters);

        byte[] masterKey = new byte[outputLength];
        generator.generateBytes(password, masterKey);

        return masterKey;
    }
}
