package com.jquinss.passwordmanager.util.password;

public class Password {
    private static final int[] LOWER_CASE_ASCII_RANGE = {97, 122};
    private static final int[] UPPER_CASE_ASCII_RANGE = {65, 90};
    private static final int[][] SYMBOL_RANGE = {{33, 47}, {58, 64}, {91, 96}, {123, 126}};
    private String password;
    private int numLowerCaseChars;
    private int numUpperCaseChars;
    private int numDigits;
    private int numSymbols;
    private int maxConsecutiveEqualChars;

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

    public int getMaxConsecutiveEqualChars() {
        return maxConsecutiveEqualChars;
    }

    public int getLength() {
        return password.length();
    }

    public Password(String password) {
        this.password = password;
        analyzePassword(this.password);
    }

    public void setPassword(String password) {
        this.password = password;
        resetCounters();
        analyzePassword(this.password);
    }

    private void analyzePassword(String password) {
        char[] charArray = password.toCharArray();

        for (char c : charArray) {
            if (isLowerCaseChar(c)) {
                numLowerCaseChars += 1;
            } else if (isUpperCaseChar(c)) {
                numUpperCaseChars += 1;
            } else if (Character.isDigit(c)) {
                numDigits += 1;
            } else if (isSymbol(c)) {
                numSymbols += 1;
            }
        }

        maxConsecutiveEqualChars = getMaxConsecutiveEqualChars(charArray, password);
    }

    private boolean isLowerCaseChar(char c) {
        return isIntInRange(c, LOWER_CASE_ASCII_RANGE[0], LOWER_CASE_ASCII_RANGE[1]);
    }

    private boolean isUpperCaseChar(char c) {
        return isIntInRange(c, UPPER_CASE_ASCII_RANGE[0], UPPER_CASE_ASCII_RANGE[1]);
    }

    private boolean isSymbol(char c) {
        return (isIntInRange(c, SYMBOL_RANGE[0][0], SYMBOL_RANGE[0][1])
                || isIntInRange(c, SYMBOL_RANGE[1][0], SYMBOL_RANGE[1][1])
                || isIntInRange(c, SYMBOL_RANGE[2][0], SYMBOL_RANGE[2][1])
                || isIntInRange(c, SYMBOL_RANGE[3][0], SYMBOL_RANGE[3][1]));
    }

    private int getMaxConsecutiveEqualChars(char[] charArray, String text) {
        int maxAbs = 1;

        for (char c : charArray) {
            int max = 1;
            while (text.contains(multiplyString(String.valueOf(c), max + 1))) { ++max; }
            if (max > maxAbs) { maxAbs = max; }
        }

        return maxAbs;
    }

    private String multiplyString(String text, int times) {
        return new String(new char[times]).replace("\0", text);
    }

    private boolean isIntInRange(int i, int min, int max) {
        return (i >= min && i <= max);
    }

    private void resetCounters() {
        this.numLowerCaseChars = 0;
        this.numUpperCaseChars = 0;
        this.numDigits = 0;
        this.numSymbols = 0;
    }

    public String toString() {
        return this.password;
    }
}
