package com.jquinss.passwordmanager.util.password;

import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PasswordGenerator {
    private static final int[] LOWER_CASE_ASCII_RANGE = {97, 122};
    private static final int[] UPPER_CASE_ASCII_RANGE = {65, 90};
    private static final int[] DIGIT_RANGE = {48, 57};
    private static final char[] SYMBOLS_ASCII = {'~', '`', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '-',
            '+', '=', '{', '[', '}', ']', '|', '\\', ':', ';', '"', '\'', '<', ',', '>', '.', '?', '/'};
    private final int numLowerCaseChars;
    private final int numUpperCaseChars;
    private final int numDigits;
    private final int numSymbols;

    private PasswordGenerator(Builder builder) {
        this.numLowerCaseChars = builder.numLowerCaseChars;
        this.numUpperCaseChars = builder.numUpperCaseChars;
        this.numDigits = builder.numDigits;
        this.numSymbols = builder.numSymbols;
    }

    public static class Builder {
        private int numLowerCaseChars;
        private int numUpperCaseChars;
        private int numDigits;
        private int numSymbols;

        public Builder numLowerCaseChars(int numLowerCaseChars) {
            validatePositiveInteger(numLowerCaseChars);
            this.numLowerCaseChars= numLowerCaseChars;
            return this;
        }

        public Builder numUpperCaseChars(int numUpperCaseChars) {
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

        public PasswordGenerator build() {
            if (this.numLowerCaseChars + this.numUpperCaseChars +
                    this.numDigits + this.numSymbols <= 0) {
                throw new RuntimeException("The password needs to have at least 1 character");
            }
            return new PasswordGenerator(this);
        }

        private void validatePositiveInteger(int num) {
            if (num < 0) {
                throw new RuntimeException("Must be a positive integer");
            }
        }
    }

    private char[] getRandomCharSampleFromIntRange(int size, int min, int max, IntFunction<String> mapper) {
        Random randGenerator = new Random();
        IntStream intStream = randGenerator.ints(size, min, max + 1);
        return intStream.mapToObj(mapper).collect(Collectors.joining()).toCharArray();
    }

    public String generatePassword() {
        char[] lowerCaseChars = getRandomCharSampleFromIntRange(numLowerCaseChars, LOWER_CASE_ASCII_RANGE[0],
                LOWER_CASE_ASCII_RANGE[1], Character::toString);
        char[] upperCaseChars = getRandomCharSampleFromIntRange(numUpperCaseChars, UPPER_CASE_ASCII_RANGE[0],
                UPPER_CASE_ASCII_RANGE[1], Character::toString);
        char[] digits = getRandomCharSampleFromIntRange(numDigits, DIGIT_RANGE[0], DIGIT_RANGE[1],
                Character:: toString);
        char[] symbols = getRandomCharSampleFromIntRange(numSymbols, 0, SYMBOLS_ASCII.length - 1,
                i -> Character.toString(SYMBOLS_ASCII[i]));

        int totalSize = lowerCaseChars.length + upperCaseChars.length + digits.length + symbols.length;
        char[] merged = mergeCharArray(totalSize, lowerCaseChars, upperCaseChars, digits, symbols);

        return createRandomStringFromCharArray(merged);
    }

    private String createRandomStringFromCharArray(char[] charArray) {
        StringBuilder text = new StringBuilder(charArray.length);

        for (char c : charArray) {
            Random randGenerator = new Random();
            int insertPos = randGenerator.nextInt(text.length() + 1);
            text.insert(insertPos, c);
        }

        return text.toString();
    }

    private char[] mergeCharArray(int totalSize, char[]... charArrays) {
        char[] mergedCharArray = new char[totalSize];
        int index = 0;
        for (char[] charArray : charArrays) {
            for (char c : charArray) {
                mergedCharArray[index++] = c;
            }
        }
        return mergedCharArray;
    }
}
