package chabssaltteog.balance_board.exception;

import jakarta.validation.ValidationException;

public class InvalidUserException extends ValidationException {
    public InvalidUserException(String message) {
        super(message);
    }
}
