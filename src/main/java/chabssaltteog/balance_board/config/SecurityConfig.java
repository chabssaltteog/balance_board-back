package chabssaltteog.balance_board.config;

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

//    private final CustomOAuth2UserService customOAuth2UserService;

    private final JwtTokenProvider jwtTokenProvider;

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
                                "/posts/**", "/comments/**", "/api/**", "/swagger-resources/**", "/swagger-ui/**",
                                "/api/user/*", "/login/oauth2/**").permitAll()
                        .anyRequest().permitAll()
                )
                .logout(
                        (logoutConfig) -> logoutConfig.logoutSuccessUrl("/")    // logout
                ).addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/oauth2/authorization/google")
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(customOAuth2UserService))
//                        .redirectionEndpoint(redirection -> redirection
//                                .baseUri("/join"))
//                );
        return http.getOrBuild();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://52.78.127.145:8080",
                "http://127.0.0.1:3000", "http://localhost:3000", "http://localhost:8080",
                "https://balance-board-front-git-develop-chabssaltteog.vercel.app", "https://balance-board-front-1wyd.vercel.app")); // 모든 origin 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
