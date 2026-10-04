package com.jquinss.passwordmanager.crypto;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
}
