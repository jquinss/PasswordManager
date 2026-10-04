package com.jquinss.passwordmanager.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jquinss.passwordmanager.config.AppConfig;
import com.jquinss.passwordmanager.crypto.Argon2Kdf;
import com.jquinss.passwordmanager.crypto.CryptoUtils;
import com.jquinss.passwordmanager.crypto.HKDF;
import com.jquinss.passwordmanager.enums.DataSourceType;
import com.jquinss.passwordmanager.factories.DataSourceFactory;
import com.jquinss.passwordmanager.util.misc.Base64ToBytesConverter;
import com.jquinss.passwordmanager.vault.metadata.VaultMetadata;
import com.jquinss.passwordmanager.vault.metadata.VaultMetadataIO;
import com.jquinss.passwordmanager.vault.metadata.VaultMetadataInfo;
import com.jquinss.passwordmanager.vault.repository.VaultRepository;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Properties;

public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationResult authenticate(String password) {
        Path vaultDbPath = Path.of(AppConfig.get("vault_db.path"));
        Path vaultMetadataPath = Path.of(AppConfig.get("vault_meta.path"));

        // check if vault exists
        if (!Files.exists(vaultDbPath) && !Files.exists(vaultMetadataPath)) {
            return new AuthenticationFailure(AuthenticationStatus.VAULT_NOT_FOUND);
        }

        // check if a partial vault exists
        if (Files.exists(vaultDbPath) ^ Files.exists(vaultMetadataPath)) {
            return new AuthenticationFailure(AuthenticationStatus.VAULT_PARTIALLY_EXISTS);
        }

        try {
            VaultMetadata vaultMetadata = VaultMetadataIO.read(vaultMetadataPath);

            Argon2Parameters parameters = new Argon2Parameters.Builder(vaultMetadata.getArgon2Param().algorithm())
                    .withSalt(Base64ToBytesConverter.base64ToBytes(vaultMetadata.getHkdfSalt()))
                    .withIterations(vaultMetadata.getArgon2Param().iterations())
                    .withMemoryAsKB(vaultMetadata.getArgon2Param().memoryKb())
                    .withParallelism(vaultMetadata.getArgon2Param().parallelism())
                    .build();

            byte[] masterKey = Argon2Kdf.deriveMasterKey(password.toCharArray(), parameters,
                    vaultMetadata.getArgon2Param().outputLength());

            // verify vault metadata integrity and authentication
            if (!verifyVaultMetadataIntegrity(masterKey, vaultMetadata) || !verifyAuthentication(masterKey, vaultMetadata)) {
                return new AuthenticationFailure(AuthenticationStatus.METADATA_INTEGRITY_FAILED);
            }

            // Derive encryption key
            byte[] encryptionKey = HKDF.deriveKey(masterKey, null, "encryption", 32);

            // Convert encryption key to Base64 string
            String encodedEncryptionKey = Base64ToBytesConverter.bytesToBase64(encryptionKey);

            VaultRepository vaultRepository = getVaultRepository(vaultDbPath, encodedEncryptionKey);

            if (!verifyDbVaultMetadataInfo(vaultRepository, vaultMetadata)) {
                return new AuthenticationFailure(AuthenticationStatus.DB_METADATA_VERIFICATION_FAILED);
            }

            return new AuthenticationSuccess(vaultRepository);
        }
        catch (IOException e) {
            logger.error(AuthenticationStatus.METADATA_READ_FAILED.getMessage(), e);
            return new AuthenticationFailure(AuthenticationStatus.METADATA_READ_FAILED);
        }
        catch (SQLException e) {
            logger.error(AuthenticationStatus.DB_CONNECTION_FAILED.getMessage(), e);
            return new AuthenticationFailure(AuthenticationStatus.DB_CONNECTION_FAILED);
        }
    }

    private boolean verifyVaultMetadataIntegrity(byte[] masterKey, VaultMetadata vaultMetadata) throws JsonProcessingException {
        // derive mac key
        byte[] macKey = HKDF.deriveKey(masterKey, null, "header-mac", 32);

        byte[] mac = Base64.getDecoder().decode(vaultMetadata.getMac());
        byte[] computedMac = CryptoUtils.computeMac(vaultMetadata, macKey);

        // check if vault.meta has been modified
        return MessageDigest.isEqual(mac, computedMac);
    }

    private boolean verifyAuthentication(byte[] masterKey, VaultMetadata vaultMetadata) {
        // Derive authentication key
        byte[] authKey = HKDF.deriveKey(masterKey, null, "authentication", 32);

        byte[] computedAuthVerifier = CryptoUtils.hmacSha256(authKey, "auth-verifier-v1");
        byte[] authVerifier = Base64.getDecoder().decode(vaultMetadata.getAuthVerifier());

        return MessageDigest.isEqual(authVerifier, computedAuthVerifier);
    }

    private boolean verifyDbVaultMetadataInfo(VaultRepository vaultRepository, VaultMetadata vaultMetadata) throws SQLException {
        // verify database vault metadata info
        VaultMetadataInfo vaultMetadataInfo = vaultRepository.getVaultMetadataInfo();

        return vaultMetadata.getVersion() == vaultMetadataInfo.version() &&
                vaultMetadata.getVaultId().equals(vaultMetadataInfo.vaultId());
    }

    private VaultRepository getVaultRepository(Path vaultDbPath, String encryptionKey) {
        // Create connection to the database
        Properties props = new Properties();
        props.setProperty("key", encryptionKey);
        DataSource dataSource = DataSourceFactory.getDataSource(DataSourceType.SQLITE,
                "jdbc:sqlite:file:" + vaultDbPath, props);
        return new VaultRepository(dataSource);
    }
}
