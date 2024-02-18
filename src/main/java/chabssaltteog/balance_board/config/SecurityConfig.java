package chabssaltteog.balance_board.config;

import chabssaltteog.balance_board.domain.MyRole;
import chabssaltteog.balance_board.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean   // 파라미터 없이 사용할 수 없기 때문에, Lambda 형식으로 작성
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(
                        (csrfConfig) -> csrfConfig.disable()
                )
                .headers(
                        (headerConfig) -> headerConfig.frameOptions(
                                frameOptionsConfig -> frameOptionsConfig.disable()
                        )
                )
                .authorizeHttpRequests((authorizeRequest) -> authorizeRequest
                        .requestMatchers("/posts/new", "/comments/save").hasRole(MyRole.USER.name())    //todo
                        .requestMatchers("/", "/css/**", "images/**", "/js/**", "/login/*", "/logout/*",
                                "/posts/**", "/comments/**", "/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(
                        (logoutConfig) -> logoutConfig.logoutSuccessUrl("/")    // logout
                )
                .oauth2Login(Customizer.withDefaults());
        return http.build();
    }
}
