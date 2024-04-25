package com.jquinss.passwordmanager.util.password;

public class PasswordSpecs {
    private int numLowerCaseChars;
    private int numUpperCaseChars;
    private int numDigits;
    private int numSymbols;

    private PasswordSpecs(Builder builder) {
        this.numLowerCaseChars = builder.numLowerCaseChars;
        this.numUpperCaseChars = builder.numUpperCaseChars;
        this.numDigits = builder.numDigits;
        this.numSymbols = builder.numSymbols;
    }

    public int getNumLowerCaseChars() {
        return numLowerCaseChars;
    }
    public int getNumUpperCaseChars() {
        return numUpperCaseChars;
    }

    public int getNumDigits() {
        return numDigits;
    }

    public int getNumSymbols() {
        return numSymbols;
    }

    public static class Builder {
        private int numLowerCaseChars;
        private int numUpperCaseChars;
        private int numDigits;
        private int numSymbols;

        public Builder numLowerCaseChars(int numLowerCaseChars) {
            validatePositiveInteger(numLowerCaseChars);
            this.numLowerCaseChars = numLowerCaseChars;
            return this;
        }

        public Builder numUppercaseChars(int numUpperCaseChars) {
            validatePositiveInteger(numUpperCaseChars);
            this.numUpperCaseChars = numUpperCaseChars;
            return this;
        }

        public Builder numDigits(int numDigits) {
            validatePositiveInteger(numDigits);
            this.numDigits = numDigits;
            return this;
        }

        public Builder numSymbols(int numSymbols) {
            validatePositiveInteger(numSymbols);
            this.numSymbols = numSymbols;
            return this;
        }

        public PasswordSpecs build() {
            if ((this.numLowerCaseChars + this.numUpperCaseChars + this.numDigits + this.numSymbols) <= 0) {
                throw new RuntimeException("The password needs to have at least 1 character");
            }
            return new PasswordSpecs(this);
        }

        private void validatePositiveInteger(int num) {
            if (num < 0) {
                throw new RuntimeException("Must be a positive integer");
            }
        }
    }
}
