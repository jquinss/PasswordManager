package com.jquinss.passwordmanager.util.password;

public class PasswordSpecs {
    private int numLowerCaseChars;
    private int numUpperCaseChars;
    private int numDigits;
    private int numSymbols;

    public int getNumLowerCaseChars() {
        return numLowerCaseChars;
    }

    public void setNumLowerCaseChars(int numLowerCaseChars) {
        validatePositiveInteger(numLowerCaseChars);
        this.numLowerCaseChars = numLowerCaseChars;
    }

    public int getNumUpperCaseChars() {
        return numUpperCaseChars;
    }

    public void setNumUpperCaseChars(int numUpperCaseChars) {
        validatePositiveInteger(numUpperCaseChars);
        this.numUpperCaseChars = numUpperCaseChars;
    }

    public int getNumDigits() {
        return numDigits;
    }

    public void setNumDigits(int numDigits) {
        validatePositiveInteger(numDigits);
        this.numDigits = numDigits;
    }

    public int getNumSymbols() {
        return numSymbols;
    }

    public void setNumSymbols(int numSymbols) {
        validatePositiveInteger(numSymbols);
        this.numSymbols = numSymbols;
    }

    private void validatePositiveInteger(int num) {
        if (num < 0) {
            throw new RuntimeException("Must be a positive integer");
        }
    }
}
