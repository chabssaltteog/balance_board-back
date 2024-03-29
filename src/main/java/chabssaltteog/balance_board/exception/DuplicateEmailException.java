package chabssaltteog.balance_board.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateEmailException extends DuplicateKeyException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
