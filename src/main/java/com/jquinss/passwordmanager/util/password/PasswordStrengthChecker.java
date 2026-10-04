package com.jquinss.passwordmanager.util.password;

import java.util.HashMap;

public class PasswordStrengthChecker {
    private final HashMap<PasswordStrength, PasswordStrengthCriteria> pwdStrengthCriteriaHashMap = new HashMap<>();
    public PasswordStrengthChecker() {
        initPwdStrengthCriteriaHashMap();
    }
    public PasswordStrength checkPasswordStrength(String password) {
        PasswordStrength pwdStrength = PasswordStrength.NONE;
        Password pwd = new Password(password);

        if (passwordMeetsStrengthCriteria(pwd, pwdStrengthCriteriaHashMap.get(PasswordStrength.EXCELLENT))) {
            pwdStrength = PasswordStrength.EXCELLENT;
        } else if (passwordMeetsStrengthCriteria(pwd, pwdStrengthCriteriaHashMap.get(PasswordStrength.GOOD))) {
            pwdStrength = PasswordStrength.GOOD;
        } else if (passwordMeetsStrengthCriteria(pwd, pwdStrengthCriteriaHashMap.get(PasswordStrength.FAIR))) {
            pwdStrength = PasswordStrength.FAIR;
        } else if (passwordMeetsStrengthCriteria(pwd, pwdStrengthCriteriaHashMap.get(PasswordStrength.LOW))) {
            pwdStrength = PasswordStrength.LOW;
        }

        return pwdStrength;
    }

    public boolean passwordMeetsStrengthCriteria(Password password, PasswordStrengthCriteria criteria) {
            return (password.getNumLowerCaseChars() >= criteria.getMinLowerCaseChars()
                    && password.getNumUpperCaseChars() >= criteria.getMinUpperCaseChars()
                    && password.getNumDigits() >= criteria.getMinDigits()
                    && password.getNumSymbols() >= criteria.getMinSymbols()
                    && password.getLength() >= criteria.getMinLength()
                    && password.getMaxConsecutiveEqualChars() <= criteria.getMaxConsecutiveEqualChars());
    }

    private void initPwdStrengthCriteriaHashMap() {
        addPasswordStrengthCriteria(PasswordStrength.EXCELLENT, new PasswordStrengthCriteria.Builder().minLength(10).minLowerCaseChars(3)
                .minUppercaseChars(3).minDigits(3).minSymbols(3).maxConsecutiveChars(2).build());
        addPasswordStrengthCriteria(PasswordStrength.GOOD, new PasswordStrengthCriteria.Builder().minLength(8).minLowerCaseChars(3)
                .minUppercaseChars(3).minDigits(3).minSymbols(3).build());
        addPasswordStrengthCriteria(PasswordStrength.FAIR, new PasswordStrengthCriteria.Builder().minLowerCaseChars(1).minUppercaseChars(1)
                .minDigits(1).build());
        addPasswordStrengthCriteria(PasswordStrength.LOW, new PasswordStrengthCriteria.Builder().minLength(6).build());
        addPasswordStrengthCriteria(PasswordStrength.NONE, new PasswordStrengthCriteria.Builder().minLength(1).build());
    }

    public void addPasswordStrengthCriteria(PasswordStrength passwordStrength, PasswordStrengthCriteria passwordStrengthCriteria) {
        pwdStrengthCriteriaHashMap.put(passwordStrength, passwordStrengthCriteria);
    }

    public PasswordStrengthCriteria getCriteria(PasswordStrength passwordStrength) {
        return pwdStrengthCriteriaHashMap.get(passwordStrength);
    }
}
