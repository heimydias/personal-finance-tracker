package dias.heimy.config.security;

import dias.heimy.domain.exception.InvalidTokenException;
import dias.heimy.domain.valueobject.JwtToken;
import dias.heimy.domain.valueobject.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtTokenProvider {

    private final SecretKey accessTokenKey;
    private final SecretKey refreshTokenKey;
    private final long accessTokenExpirationMinutes;
    private final long refreshTokenExpirationDays;

    public JwtTokenProvider(
            @Value("${app.jwt.access-token.secret}") String accessTokenSecret,
            @Value("${app.jwt.refresh-token.secret}") String refreshTokenSecret,
            @Value("${app.jwt.access-token.expiration-minutes:60}") long accessTokenExpirationMinutes,
            @Value("${app.jwt.refresh-token.expiration-days:30}") long refreshTokenExpirationDays) {

        this.accessTokenKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshTokenKey = Keys.hmacShaKeyFor(refreshTokenSecret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    public JwtToken generateAccessToken(String email, String role) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(accessTokenExpirationMinutes, ChronoUnit.MINUTES);

        String token = Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(accessTokenKey)
                .compact();

        log.debug("Token de acesso gerado para usuário: {}", email);
        return new JwtToken(token, issuedAt, expiresAt);
    }

    public RefreshToken generateRefreshToken(String email) {
        return generateRefreshToken(email, refreshTokenExpirationDays);
    }

    public RefreshToken generateRefreshToken(String email, long customExpirationDays) {
        Instant expiresAt = Instant.now().plus(customExpirationDays, ChronoUnit.DAYS);

        String token = Jwts.builder()
                .subject(email)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiresAt))
                .signWith(refreshTokenKey)
                .compact();

        log.debug("Token de renovação gerado para usuário: {} com expiração de {} dias", email, customExpirationDays);
        return new RefreshToken(token, expiresAt);
    }

    public String extractEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(accessTokenKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid access token", e);
        }
    }

    public String extractRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(accessTokenKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("role", String.class);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid access token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(accessTokenKey).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT não suportado: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token JWT malformado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("String de claims JWT está vazia: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Erro na validação do JWT: {}", e.getMessage());
        }
        return false;
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser().verifyWith(refreshTokenKey).build().parseSignedClaims(refreshToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Token de renovação expirado: {}", e.getMessage());
            throw new InvalidTokenException("Refresh token expired", e);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token", e);
        }
    }

    public Authentication getAuthenticationFromToken(String token) {
        try {
            String email = extractEmailFromToken(token);
            String role = extractRoleFromToken(token);

            Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

            return new UsernamePasswordAuthenticationToken(email, null, authorities);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid access token", e);
        }
    }

    public String extractEmailFromRefreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(refreshTokenKey)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token", e);
        }
    }
}
