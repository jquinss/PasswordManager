package com.jquinss.passwordmanager.util.password;

import java.util.HashMap;

public class PasswordStrengthChecker {
    private HashMap<PasswordStrength, PasswordStrengthCriteria> criteria = new HashMap<>();
    public PasswordStrengthChecker() {
        initializeCriteria();
    }
    public PasswordStrength checkPasswordStrength(String password) {
        PasswordStrength strength = PasswordStrength.NONE;
        Password pwd = new Password(password);

        if (passwordMeetsStrengthCriteria(pwd, criteria.get(PasswordStrength.EXCELLENT))) {
            strength = PasswordStrength.EXCELLENT;
        } else if (passwordMeetsStrengthCriteria(pwd, criteria.get(PasswordStrength.GOOD))) {
            strength = PasswordStrength.GOOD;
        } else if (passwordMeetsStrengthCriteria(pwd, criteria.get(PasswordStrength.FAIR))) {
            strength = PasswordStrength.FAIR;
        } else if (passwordMeetsStrengthCriteria(pwd, criteria.get(PasswordStrength.LOW))) {
            strength = PasswordStrength.LOW;
        }

        return strength;
    }

    private boolean passwordMeetsStrengthCriteria(Password pwd, PasswordStrengthCriteria criteria) {
            return (pwd.getNumLowerCaseChars() >= criteria.getMinLowerCaseChars()
                    && pwd.getNumUpperCaseChars() >= criteria.getMinUpperCaseChars()
                    && pwd.getNumDigits() >= criteria.getMinDigits()
                    && pwd.getNumSymbols() >= criteria.getMinSymbols()
                    && pwd.getLength() >= criteria.getMinLength()
                    && pwd.getMaxConsecutiveEqualChars() <= criteria.getMaxConsecutiveEqualChars());
    }

    private void initializeCriteria() {
        addCriteria(PasswordStrength.EXCELLENT, new PasswordStrengthCriteria.Builder().minLength(10).minLowerCaseChars(3)
                .minUppercaseChars(3).minDigits(3).minSymbols(3).maxConsecutiveChars(2).build());
        addCriteria(PasswordStrength.GOOD, new PasswordStrengthCriteria.Builder().minLength(8).minLowerCaseChars(3)
                .minUppercaseChars(3).minDigits(3).minSymbols(3).build());
        addCriteria(PasswordStrength.FAIR, new PasswordStrengthCriteria.Builder().minLowerCaseChars(1).minUppercaseChars(1)
                .minDigits(1).build());
        addCriteria(PasswordStrength.LOW, new PasswordStrengthCriteria.Builder().minLength(6).build());
        addCriteria(PasswordStrength.NONE, new PasswordStrengthCriteria.Builder().minLength(1).build());
    }

    public void addCriteria(PasswordStrength pwdStrength, PasswordStrengthCriteria pwdStrengthCriteria) {
        criteria.put(pwdStrength, pwdStrengthCriteria);
    }

    public PasswordStrengthCriteria getCriteria(PasswordStrength pwdStrength) {
        return criteria.get(pwdStrength);
    }
}
