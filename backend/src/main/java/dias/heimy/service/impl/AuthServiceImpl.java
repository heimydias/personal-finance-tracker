package dias.heimy.service.impl;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.valueobject.JwtToken;
import dias.heimy.domain.valueobject.RefreshToken;
import dias.heimy.dto.request.LoginRequest;
import dias.heimy.dto.request.RefreshTokenRequest;
import dias.heimy.dto.response.AuthenticationResponse;
import dias.heimy.repository.UserRepository;
import dias.heimy.service.AuthService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        log.info("Tentando autenticar usuário: {}", request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        JwtToken accessToken = jwtTokenProvider.generateAccessToken(
                user.getEmail(), user.getRole().getAuthority());
        RefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        log.info("Login realizado com sucesso para usuário: {}", request.email());

        return AuthenticationResponse.of(
                accessToken.token(),
                refreshToken.token(),
                java.time.Duration.between(Instant.now(), accessToken.expiresAt())
                        .getSeconds(),
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getRole().name());
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        log.info("Tentando renovar token");

        String email = jwtTokenProvider.extractEmailFromRefreshToken(request.refreshToken());
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        JwtToken newAccessToken = jwtTokenProvider.generateAccessToken(
                user.getEmail(), user.getRole().getAuthority());
        RefreshToken newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        log.info("Token renovado com sucesso para usuário: {}", user.getEmail());

        return AuthenticationResponse.of(
                newAccessToken.token(),
                newRefreshToken.token(),
                java.time.Duration.between(Instant.now(), newAccessToken.expiresAt())
                        .getSeconds(),
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getRole().name());
    }
}
