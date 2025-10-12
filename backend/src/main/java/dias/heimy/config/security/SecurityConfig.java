package dias.heimy.config.security;

import static dias.heimy.constants.PathConstants.AUTH_REFRESH_TOKEN;
import static dias.heimy.constants.PathConstants.AUTH_TOKEN;
import static dias.heimy.constants.PathConstants.TRANSACTIONS;
import static dias.heimy.constants.PathConstants.TRANSACTIONS_BALANCE;
import static dias.heimy.constants.PathConstants.TRANSACTIONS_BY_ID;
import static dias.heimy.constants.PathConstants.USERS;
import static dias.heimy.constants.PathConstants.USERS_BY_ID;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import dias.heimy.repository.UserRepository;
import dias.heimy.service.CustomUserDetailsService;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                AUTH_TOKEN,
                                AUTH_REFRESH_TOKEN,
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/actuator/health",
                                "/error",
                                "/",
                                "/docs",
                                "/swagger",
                                "/swagger-ui")
                        .permitAll()
                        .requestMatchers(POST, USERS)
                        .permitAll()
                        .requestMatchers(GET, USERS)
                        .hasRole("ADMIN")
                        .requestMatchers(GET, USERS_BY_ID)
                        .authenticated()
                        .requestMatchers(PUT, USERS_BY_ID)
                        .authenticated()
                        .requestMatchers(DELETE, USERS_BY_ID)
                        .authenticated()
                        .requestMatchers(POST, TRANSACTIONS)
                        .authenticated()
                        .requestMatchers(GET, TRANSACTIONS)
                        .authenticated()
                        .requestMatchers(GET, TRANSACTIONS_BY_ID)
                        .authenticated()
                        .requestMatchers(PUT, TRANSACTIONS_BY_ID)
                        .authenticated()
                        .requestMatchers(DELETE, TRANSACTIONS_BY_ID)
                        .authenticated()
                        .requestMatchers(GET, TRANSACTIONS_BALANCE)
                        .authenticated()
                        .anyRequest()
                        .authenticated())
                .build();
    }

    @Bean
    public CustomUserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOriginPatterns(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));

        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
