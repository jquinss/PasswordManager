package com.jquinss.passwordmanager.security;

import com.jquinss.passwordmanager.data.UserProfile;
import com.jquinss.passwordmanager.managers.SettingsManager;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;

import java.sql.SQLException;
import java.util.Arrays;

public class Authenticator {
    public boolean authenticate(UserProfile userProfile, String password) throws SQLException {
        byte[] salt = userProfile.getPasswordSalt();

        byte[] computedPwdHash = CryptoUtils.getHashFromString(password, 32, salt);
        byte[] pwdHash = userProfile.getPasswordHash();

        return Arrays.equals(computedPwdHash, pwdHash);
    }
}
