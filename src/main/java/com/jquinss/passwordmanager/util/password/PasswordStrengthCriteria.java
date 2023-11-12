package com.jquinss.passwordmanager.util.password;

public class PasswordStrengthCriteria {
    private int minLength;
    private int minLowerCaseChars;
    private int minUpperCaseChars;
    private int minDigits;
    private int minSymbols;
    private int maxConsecutiveEqualChars;

    private PasswordStrengthCriteria(Builder builder) {
        this.minLength = builder.minLength;
        this.minLowerCaseChars = builder.minLowerCaseChars;
        this.minUpperCaseChars = builder.minUpperCaseChars;
        this.minDigits = builder.minDigits;
        this.minSymbols = builder.minSymbols;
        this.maxConsecutiveEqualChars = builder.maxConsecutiveEqualChars;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMinLowerCaseChars() {
        return minLowerCaseChars;
    }

    public int getMinUpperCaseChars() {
        return minUpperCaseChars;
    }

    public int getMinDigits() {
        return minDigits;
    }

    public int getMinSymbols() {
        return minSymbols;
    }

    public int getMaxConsecutiveEqualChars() {
        return maxConsecutiveEqualChars;
    }

    public String toString() {
        return "Minimum password length: " + minLength +
                "\nMinimum number of lower-case characters: " + minLowerCaseChars +
                "\nMinimum number of upper-case characters: " + minUpperCaseChars +
                "\nMinimum number of digits: " + minDigits +
                "\nMinimum number of symbols: " + minSymbols +
                "\nMaximum number of equal consecutive characters: " + maxConsecutiveEqualChars;
    }

    public static class Builder {
        private int minLength;
        private int minLowerCaseChars;
        private int minUpperCaseChars;
        private int minDigits;
        private int minSymbols;
        private int maxConsecutiveEqualChars = 20;

        public Builder minLength(int minLength) {
            validatePositiveInteger(minLength);
            this.minLength = minLength;
            return this;
        }

        public Builder minLowerCaseChars(int minLowerCaseChars) {
            validatePositiveInteger(minLowerCaseChars);
            this.minLowerCaseChars = minLowerCaseChars;
            return this;
        }

        public Builder minUppercaseChars(int minUpperCaseChars) {
            validatePositiveInteger(minUpperCaseChars);
            this.minUpperCaseChars = minUpperCaseChars;
            return this;
        }

        public Builder minDigits(int minDigits) {
            validatePositiveInteger(minDigits);
            this.minDigits = minDigits;
            return this;
        }

        public Builder minSymbols(int minSymbols) {
            validatePositiveInteger(minSymbols);
            this.minSymbols = minSymbols;
            return this;
        }

        public Builder maxConsecutiveChars(int maxConsecutiveEqualChars) {
            validatePositiveInteger(maxConsecutiveEqualChars);
            this.maxConsecutiveEqualChars = maxConsecutiveEqualChars;
            return this;
        }

        public PasswordStrengthCriteria build() {
            return new PasswordStrengthCriteria(this);
        }

        private void validatePositiveInteger(int num){
            if (num < 0) {
                throw new RuntimeException("Must be a positive integer");
            }
        }
    }
}
