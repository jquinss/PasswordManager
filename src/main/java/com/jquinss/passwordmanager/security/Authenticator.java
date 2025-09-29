package com.jquinss.passwordmanager.security;

import com.jquinss.passwordmanager.data.UserProfile;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.managers.SettingsManager;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public class Authenticator {
    public boolean authenticate(UserProfile userProfile, String password) throws SQLException {
        byte[] salt = userProfile.getPasswordSalt();
        int hashLength = SettingsManager.getInstance().getPasswordHashLengthInBytes();

        byte[] computedPwdHash = CryptoUtils.getHashFromString(password, hashLength, salt);
        byte[] pwdHash = userProfile.getPasswordHash();

        return Arrays.equals(computedPwdHash, pwdHash);
    }
}
