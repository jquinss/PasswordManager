package com.jquinss.passwordmanager.managers;

import java.nio.file.Path;
import com.jquinss.passwordmanager.util.misc.OSChecker;
import com.jquinss.passwordmanager.util.password.PasswordStrength;

public class SettingsManager {
    private static final String APP_FOLDER_NAME = "PasswordManager";
    private static final String DATA_FOLDER_NAME = "data";
    private static final String DB_NAME = "database.db";
    private static final SettingsManager settingsManager = new SettingsManager();
    private final int passwordHashLengthInBytes = 32;
    private final int saltLengthInBytes = 64;
    private final String keyPairAlgorithm = "RSA";
    private final String symmetricEncryptionAlgorithm = "AES/CBC/PKCS5Padding";
    private final int ivParameterSpecLengthInBytes = 16;
    private final int keyPairLengthInBits = 2048;

    private static PasswordStrength minPasswordStrength = PasswordStrength.EXCELLENT;

    private SettingsManager() {}

    public static SettingsManager getInstance() {
        return settingsManager;
    }

    public String getDatabasePath() {
        return Path.of(OSChecker.getOSDataDirectory(), APP_FOLDER_NAME, DATA_FOLDER_NAME, DB_NAME).toString();
    }

    public String getDatabaseDir() {
        return Path.of(OSChecker.getOSDataDirectory(), APP_FOLDER_NAME, DATA_FOLDER_NAME).toString();
    }

    public int getPasswordHashLengthInBytes() {
        return passwordHashLengthInBytes;
    }

    public int getSaltLengthInBytes() {
        return saltLengthInBytes;
    }

    public PasswordStrength getMinPasswordStrength() {
        return minPasswordStrength;
    }

    public String getKeyPairAlgorithm() {
        return keyPairAlgorithm;
    }

    public String getSymmetricEncryptionAlgorithm() {
        return symmetricEncryptionAlgorithm;
    }

    public int getIvParameterSpecLengthInBytes() {
        return ivParameterSpecLengthInBytes;
    }

    public int getKeyPairLengthInBits() {
        return keyPairLengthInBits;
    }
}
