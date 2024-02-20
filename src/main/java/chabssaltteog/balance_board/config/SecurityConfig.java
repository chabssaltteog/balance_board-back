package chabssaltteog.balance_board.config;

import chabssaltteog.balance_board.domain.MyRole;
import chabssaltteog.balance_board.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean   // 파라미터 없이 사용할 수 없기 때문에, Lambda 형식으로 작성
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .cors(
                        (cors -> cors.configurationSource(corsConfigurationSource()))
                )
                .csrf(
                        (csrfConfig) -> csrfConfig.disable()
                )
                .headers(
                        (headerConfig) -> headerConfig.frameOptions(
                                frameOptionsConfig -> frameOptionsConfig.disable()
                        )
                )
                .authorizeHttpRequests((authorizeRequest) -> authorizeRequest
                        .requestMatchers("/posts/new", "/comments/save").hasRole(MyRole.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/", "/css/**", "images/**", "/js/**", "/login/*", "/logout/*",
                                "/posts/**", "/comments/**", "/api/**", "/swagger-resources/**", "/swagger-ui/**",
                                "/api/user/*", "api/user/validate", "/login/oauth2/code/google", "/api/user/test").permitAll()
                        .anyRequest().permitAll()
                )
                .logout(
                        (logoutConfig) -> logoutConfig.logoutSuccessUrl("/")    // logout
                )
                .oauth2Login(Customizer.withDefaults());
        return http.getOrBuild();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://52.78.127.145:8080", "http://52.78.127.145:8080/api/user/test",
                "http://52.78.127.145:8080/api/user/validate",
                "http://52.78.127.145:8080/api/user/login",
                "http://52.78.127.145:8080/api/user/register", "http://52.78.127.145:8080/login/oauth2/code/google",
                "http://127.0.0.1:3000", "http://localhost:3000")); // 모든 origin 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
