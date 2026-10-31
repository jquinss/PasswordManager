package com.jquinss.passwordmanager.crypto;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import com.jquinss.passwordmanager.vault.metadata.VaultMetadata;
import com.jquinss.passwordmanager.vault.metadata.VaultMetadataSerializer;

public class CryptoUtils {
    private CryptoUtils() {}

    public static byte[] computeMac(VaultMetadata metadata, byte[] headerMacKey) throws JsonProcessingException {
        try {
            byte[] canonical = VaultMetadataSerializer.canonalizeWithoutMac(metadata);
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(headerMacKey, "HmacSHA256"));
            return mac.doFinal(canonical);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);

        }
    }

    public static byte[] hmacSha256(byte[] key, String message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);

        }
    }

    public static String generateFileHash(Path filePath, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        try (InputStream is = Files.newInputStream(filePath);
             DigestInputStream dis = new DigestInputStream(is, digest);
        ) {
            byte[] buffer = new byte[8192];

            while (dis.read(buffer) != -1) {
                // DigestInputStream updates digest automatically
            }
        }

        return HexFormat.of().formatHex(digest.digest());
    }
}
