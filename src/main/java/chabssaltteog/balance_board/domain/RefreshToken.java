package chabssaltteog.balance_board.domain;

import chabssaltteog.balance_board.exception.InvalidRefreshTokenException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
//@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "refresh_token_id")
    private Long id;

//    @Column(name = "user_id")
    private Long userId;

//    @Column(name = "token")
    private String token;

    public void validateSameToken(String token) {
        if (!this.token.equals(token)) {
            throw new InvalidRefreshTokenException("refresh token invalid");
        }
    }
}
