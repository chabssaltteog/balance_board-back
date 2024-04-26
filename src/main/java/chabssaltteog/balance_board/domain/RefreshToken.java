package chabssaltteog.balance_board.domain;

import chabssaltteog.balance_board.exception.InvalidRefreshTokenException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "refresh_token")
@Entity
//@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 14)  //14일
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long tokenId;

    private String token;

//    @Indexed
    @Column(name = "user_id")
    private Long userId;

    public void updateRefreshToken(String token) {
        this.token = token;
    }

    public void validateSameToken(String token) {
        if (!this.token.equals(token)) {
            throw new InvalidRefreshTokenException("refresh token invalid");
        }
    }
}
