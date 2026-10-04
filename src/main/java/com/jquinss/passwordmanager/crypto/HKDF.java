package com.jquinss.passwordmanager.crypto;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;

import java.nio.charset.StandardCharsets;

public class HKDF {
    private HKDF() {}

    /**
     * Derive a key using HDKF-SHA256
     * @param ikm input key material (master key)
     * @param salt optional salt (can be null)
     * @param info context string (must be unique per purpose)
     * @param length number of bytes to derive
     */

    public static byte[] deriveKey(
            byte[] ikm,
            byte[] salt,
            String info,
            int length
    ) {
        if (length <= 0) {
            throw new IllegalArgumentException("Invalid key length");
        }

        HKDFBytesGenerator hkdf = new HKDFBytesGenerator(new SHA256Digest());
        HKDFParameters params = new HKDFParameters(ikm, salt, info.getBytes(StandardCharsets.UTF_8));
        hkdf.init(params);

        byte[] output = new byte[length];
        hkdf.generateBytes(output, 0, length);

        return output;
    }
}
