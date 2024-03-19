package chabssaltteog.balance_board.config;

import chabssaltteog.balance_board.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

//JWT를 통해 username + password 인증을 수행한다
//해당 토큰의 인증 정보(Authentication)를 SecurityContext에 저장

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final String path = "api/user/login/token/";
    private final String path2 = "api/user/refresh";
    private final String path3 = "api/user/login";
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String token = resolveToken((HttpServletRequest) request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            log.info("authentication setting----");
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (token == null) {
            log.info("token == null");
        } else if (((HttpServletRequest) request).getRequestURI().contains(path) ||
                ((HttpServletRequest) request).getRequestURI().contains(path2) ||
                ((HttpServletRequest) request).getRequestURI().contains(path3)) {
            log.info("---- refresh token / Login filter ----");
        }
        else {
            // 토큰이 유효하지 않거나 만료된 경우 401 응답을 반환
            ((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value(), "토큰이 만료되었거나 유효하지 않습니다.");
            return;
        }
        chain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
