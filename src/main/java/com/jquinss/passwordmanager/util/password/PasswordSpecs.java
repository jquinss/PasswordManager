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
        this.numLowerCaseChars = numLowerCaseChars;
    }

    public int getNumUpperCaseChars() {
        return numUpperCaseChars;
    }

    public void setNumUpperCaseChars(int numUpperCaseChars) {
        this.numUpperCaseChars = numUpperCaseChars;
    }

    public int getNumDigits() {
        return numDigits;
    }

    public void setNumDigits(int numDigits) {
        this.numDigits = numDigits;
    }

    public int getNumSymbols() {
        return numSymbols;
    }

    public void setNumSymbols(int numSymbols) {
        this.numSymbols = numSymbols;
    }
}
