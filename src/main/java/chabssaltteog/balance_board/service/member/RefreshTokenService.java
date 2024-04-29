package chabssaltteog.balance_board.service.member;

import chabssaltteog.balance_board.domain.member.Member;
import chabssaltteog.balance_board.domain.member.RefreshToken;
import chabssaltteog.balance_board.exception.InvalidRefreshTokenException;
import chabssaltteog.balance_board.exception.InvalidUserException;
import chabssaltteog.balance_board.repository.MemberRepository;
import chabssaltteog.balance_board.repository.RefreshTokenRepository;
import chabssaltteog.balance_board.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserId(userId);
        if (optionalRefreshToken.isEmpty()) {   // 최초 로그인
            log.info("==최초 로그인==");
            log.info("refreshToken save to DB");
            refreshTokenRepository.save(RefreshToken.builder().userId(userId).token(token).build());
            return;
        }
        log.info("Refresh TOKEN 업데이트");
        RefreshToken refreshToken = optionalRefreshToken.get();
        refreshToken.updateRefreshToken(token);
//        refreshTokenRepository.delete(optionalRefreshToken.get());  // 삭제 후 저장
//        refreshTokenRepository.save(RefreshToken.builder().userId(userId).token(token).build());
    }

    public Authentication getAuthentication(String accessToken) {
        return jwtTokenProvider.getAuthentication(accessToken);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return jwtTokenProvider.validateToken(refreshToken);
    }
}
