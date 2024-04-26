package chabssaltteog.balance_board.config.oauth;

import chabssaltteog.balance_board.domain.oauth.PrincipalDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        log.info("-- Oauth2 Login Success --");
        log.info("principal.getName = {}", principal.getName());
        if (principal.getMember().getNickname() == null | principal.getMember().getGender() == null
                | principal.getMember().getBirthYear() == null) {
            log.info("-- New User --");
            log.info("principal.getName = {}", principal.getName());
            response.sendRedirect("/");  //todo 리다이렉트 uri 수정
        }
    }
}
