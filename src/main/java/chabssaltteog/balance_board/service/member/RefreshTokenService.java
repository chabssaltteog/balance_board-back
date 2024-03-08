package chabssaltteog.balance_board.service.member;

import chabssaltteog.balance_board.domain.Member;
import chabssaltteog.balance_board.domain.RefreshToken;
import chabssaltteog.balance_board.exception.InvalidRefreshTokenException;
import chabssaltteog.balance_board.exception.InvalidUserException;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.repository.RefreshTokenRepository;
import chabssaltteog.balance_board.util.JwtToken;
import chabssaltteog.balance_board.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Transactional
    public Long matches(String refreshToken, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidUserException("사용자 정보가 올바르지 않습니다."));
        Long userId = member.getUserId();

        RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidRefreshTokenException("refresh token이 유효하지 않습니다."));

        if (!jwtTokenProvider.validateToken(savedToken.getToken())) {
            refreshTokenRepository.delete(savedToken);
            throw new InvalidRefreshTokenException("RefreshToken Problem / login required");
        }
        savedToken.validateSameToken(refreshToken);
        return userId;
    }

    public String generateToken(Authentication authentication) {
        return jwtTokenProvider.generateAccessToken(authentication);
    }

    @Transactional
    public void saveToken(String token, Long userId) {
        RefreshToken refreshToken = RefreshToken.builder().id(userId).token(token).build();
        refreshTokenRepository.save(refreshToken);
    }
}
