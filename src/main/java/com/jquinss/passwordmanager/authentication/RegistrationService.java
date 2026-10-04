package com.jquinss.passwordmanager.authentication;

import com.jquinss.passwordmanager.config.AppConfig;
import com.jquinss.passwordmanager.crypto.Argon2Kdf;
import com.jquinss.passwordmanager.crypto.Argon2Param;
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
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Properties;
import java.util.UUID;

public class RegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    private boolean overrideVault = false;
    public RegistrationResult register(String password) {
        Path vaultDbPath = Path.of(AppConfig.get("vault_db.path"));
        Path vaultMetadataPath = Path.of(AppConfig.get("vault_meta.path"));

        if (!overrideVault) {
            if (Files.exists(vaultDbPath) && Files.exists(vaultMetadataPath)) {
                logger.warn(RegistrationStatus.VAULT_ALREADY_EXISTS.getMessage());
                return new RegistrationFailure(RegistrationStatus.VAULT_ALREADY_EXISTS);
            }

            if (Files.exists(vaultDbPath) || Files.exists(vaultMetadataPath)) {
                logger.warn(RegistrationStatus.VAULT_PARTIALLY_EXISTS.getMessage());
                return new RegistrationFailure(RegistrationStatus.VAULT_PARTIALLY_EXISTS);
            }
        }
        else {
            try {
                deleteVault(vaultDbPath, vaultMetadataPath);
            }
            catch (IOException e) {
                logger.error(RegistrationStatus.VAULT_DELETION_FAILED.getMessage());
                return new RegistrationFailure(RegistrationStatus.VAULT_DELETION_FAILED);
            }

        }

        // Generate salt
        String hkdfSalt = Base64ToBytesConverter.bytesToBase64(Argon2Kdf.generateSalt(16));

        // Generate Argon2Parameters
        Argon2Param argon2Param = new Argon2Param(2, 65536, 3, 1, 16);
        Argon2Parameters parameters = new Argon2Parameters.Builder(argon2Param.algorithm())
                .withSalt(Base64ToBytesConverter.base64ToBytes(hkdfSalt))
                .withIterations(argon2Param.iterations())
                .withMemoryAsKB(argon2Param.memoryKb())
                .withParallelism(argon2Param.parallelism())
                .build();

        // Generate Master key
        byte[] masterKey = Argon2Kdf.deriveMasterKey(password.toCharArray(), parameters, argon2Param.outputLength());

        // Generate Authentication key
        byte[] authKey = HKDF.deriveKey(masterKey, null, "authentication", 32);

        // Generate Authentication verifier
        byte[] authVerifier = CryptoUtils.hmacSha256(authKey, "auth-verifier-v1");

        // Generate mac key
        byte[] macKey = HKDF.deriveKey(masterKey, null, "header-mac", 32);

        try {
            VaultMetadata vaultMetadata = createVaultMetaData(argon2Param, hkdfSalt, authVerifier,
                    macKey, vaultMetadataPath);
            logger.info("Vault metadata has been created");
            createVaultDb(vaultMetadata, masterKey, vaultDbPath);
            logger.info("Vault database has been created");
        }
        catch (IOException e) {
            logger.error(RegistrationStatus.METADATA_CREATION_FAILED.getMessage(), e);
            cleanupPartialVault(vaultDbPath, vaultMetadataPath);
            return new RegistrationFailure(RegistrationStatus.METADATA_CREATION_FAILED);
        }
        catch (SQLException e) {
            logger.error(RegistrationStatus.DB_CREATION_FAILED.getMessage(), e);
            cleanupPartialVault(vaultDbPath, vaultMetadataPath);
            return new RegistrationFailure(RegistrationStatus.DB_CREATION_FAILED);
        }
        finally {
            // reset overrideVault flag
            overrideVault = false;
        }

        return new RegistrationSuccess();
    }

    public RegistrationResult register(String password, boolean overrideVault) {
        this.overrideVault = overrideVault;
        return this.register(password);
    }

    private VaultMetadata createVaultMetaData(Argon2Param argon2Param, String hkdfSalt, byte[] authVerifier,
                                              byte[] macKey, Path vaultMetadataPath) throws IOException {
        VaultMetadata vaultMetadata = new VaultMetadata(1, argon2Param, hkdfSalt,
                UUID.randomUUID().toString(), Base64ToBytesConverter.bytesToBase64(authVerifier));
        vaultMetadata.setMac(Base64.getEncoder().encodeToString(CryptoUtils.computeMac(vaultMetadata, macKey)));
        VaultMetadataIO.createNew(vaultMetadata, vaultMetadataPath);

        return vaultMetadata;
    }

    private void createVaultDb(VaultMetadata vaultMetadata, byte[] masterKey, Path vaultDbPath) throws SQLException {
        // Derive encryption key
        byte[] encryptionKey = HKDF.deriveKey(masterKey, null, "encryption", 32);

        // Convert encryption key to Base64 string
        String encodedEncryptionKey = Base64ToBytesConverter.bytesToBase64(encryptionKey);

        // Create connection to the database
        Properties props = new Properties();
        props.setProperty("key", encodedEncryptionKey);

        DataSource dataSource = DataSourceFactory.getDataSource(DataSourceType.SQLITE,
                "jdbc:sqlite:file:" + vaultDbPath, props);

        // create vault repository
        VaultRepository vaultRepository = new VaultRepository(dataSource);
        vaultRepository.initialize();

        // set vault metadata in database
        long creationTime = java.time.Instant.now().getEpochSecond();
        VaultMetadataInfo vaultMetadataInfo = new VaultMetadataInfo(vaultMetadata.getVaultId(),
                vaultMetadata.getVersion(), creationTime, creationTime);
        vaultRepository.addVaultMetadataInfo(vaultMetadataInfo);
    }

    private void deleteVault(Path vaultDbPath, Path vaultMetadataPath) throws IOException {
        Files.deleteIfExists(vaultDbPath);
        Files.deleteIfExists(vaultMetadataPath);
    }

    private void cleanupPartialVault(Path vaultDbPath, Path vaultMetadataPath) {
        try {
            deleteVault(vaultDbPath, vaultMetadataPath);
        }
        catch (IOException e) {
            logger.error("Failed to clean up partially created vault. Database: {}, Metadata: {}",
                    vaultDbPath, vaultMetadataPath, e);
        }
    }
}
