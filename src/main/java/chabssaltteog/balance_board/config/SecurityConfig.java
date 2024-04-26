package chabssaltteog.balance_board.config;

import chabssaltteog.balance_board.config.oauth.OAuth2LoginSuccessHandler;
import chabssaltteog.balance_board.service.oauth.OAuth2MemberService;
import chabssaltteog.balance_board.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {


    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2MemberService oAuth2MemberService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean   // 파라미터 없이 사용할 수 없기 때문에, Lambda 형식으로 작성
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .httpBasic((httpBasic -> httpBasic.disable()))
                .cors(
                        (cors -> cors.configurationSource(corsConfigurationSource()))
                )
                .csrf(
                        (csrfConfig) -> csrfConfig.disable()
                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(
                        (headerConfig) -> headerConfig.frameOptions(
                                frameOptionsConfig -> frameOptionsConfig.disable()
                        )
                )
                .authorizeHttpRequests((authorizeRequest) -> authorizeRequest
//                        .requestMatchers("/posts/new", "/comments/save").hasRole(MyRole.ADMIN.name())
                        .requestMatchers("/", "/css/**", "images/**", "/js/**", "/login/*", "/logout/*",
                                "/api/**", "/swagger-resources/**", "/swagger-ui/**",
                                "/login/oauth2/**", "/oauth2/**").permitAll()
                        .anyRequest().permitAll()
                )
                .logout(
                        (logoutConfig) -> logoutConfig.logoutSuccessUrl("/")    // logout
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(new OAuth2LoginSuccessHandler())
//                        .loginPage("login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2MemberService))
                );
        return http.getOrBuild();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
