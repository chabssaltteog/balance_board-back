package chabssaltteog.balance_board.exception;

import jakarta.validation.ValidationException;

public class ValidUserException extends ValidationException {
    public ValidUserException(String message) {
        super(message);
    }
}
