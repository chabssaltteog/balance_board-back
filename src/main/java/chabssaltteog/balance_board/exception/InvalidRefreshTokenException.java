package chabssaltteog.balance_board.exception;

import jakarta.validation.ValidationException;

public class InvalidRefreshTokenException extends ValidationException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
