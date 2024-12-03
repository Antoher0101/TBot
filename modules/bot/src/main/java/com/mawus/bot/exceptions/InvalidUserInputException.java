package com.mawus.bot.exceptions;

public class InvalidUserInputException extends RuntimeException {
    private final String userInput;
    private final String reason;

    public InvalidUserInputException(String message, String userInput, String reason) {
        super(message);
        this.userInput = userInput;
        this.reason = reason;
    }

    public String getUserInput() {
        return userInput;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return super.toString() + " [User Input: " + userInput + ", Reason: " + reason + "]";
    }
}