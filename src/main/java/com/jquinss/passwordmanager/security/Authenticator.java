package com.jquinss.passwordmanager.security;

import com.jquinss.passwordmanager.data.User;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.managers.SettingsManager;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public class Authenticator {
    public boolean authenticate(String username, String password) throws SQLException {
        boolean validCredentials = false;

        Optional<User> optional = DatabaseManager.getInstance().getUserByName(username);

        if (optional.isPresent()) {
            User user = optional.get();
            byte[] salt = user.getPasswordSalt();
            int hashLength = SettingsManager.getInstance().getPasswordHashLength();

            byte[] computedPwdHash = CryptoUtils.getHashFromString(password, hashLength, salt);
            byte[] pwdHash = user.getPasswordHash();

            validCredentials = Arrays.equals(computedPwdHash, pwdHash);
        }

        return validCredentials;
    }
}
