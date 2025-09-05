package dias.heimy.service.impl;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.exception.InvalidTokenException;
import dias.heimy.domain.exception.UserAlreadyExistsException;
import dias.heimy.domain.exception.UserNotFoundException;
import dias.heimy.domain.valueobject.JwtToken;
import dias.heimy.domain.valueobject.RefreshToken;
import dias.heimy.dto.mapper.UserMapper;
import dias.heimy.dto.request.LoginRequest;
import dias.heimy.dto.request.RefreshTokenRequest;
import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.response.AuthenticationResponse;
import dias.heimy.dto.response.UserResponse;
import dias.heimy.repository.UserRepository;
import dias.heimy.service.AuthService;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserResponse register(UserRegisterRequest request) {
        log.info("Tentando registrar usuário: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        log.info("Registro realizado com sucesso para usuário: {}", request.email());

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    @Override
    public AuthenticationResponse login(LoginRequest request) {
        log.info("Tentando fazer login para usuário: {}", request.email());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            User user = userRepository
                    .findByEmail(request.email())
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            JwtToken accessToken = jwtTokenProvider.generateAccessToken(authentication);
            RefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(request.email());

            log.info("Login realizado com sucesso para usuário: {}", request.email());

            return AuthenticationResponse.of(
                    accessToken.token(),
                    refreshToken.token(),
                    ChronoUnit.SECONDS.between(accessToken.issuedAt(), accessToken.expiresAt()),
                    user.getEmail(),
                    user.getRole().getRoleName());

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @Transactional
    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        log.info("Tentando renovar token");

        String refreshTokenStr = request.refreshToken();

        if (!jwtTokenProvider.validateRefreshToken(refreshTokenStr)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String email = jwtTokenProvider.extractEmailFromRefreshToken(refreshTokenStr);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));

        JwtToken accessToken = jwtTokenProvider.generateAccessToken(
                user.getEmail(), user.getRole().getAuthority());
        RefreshToken newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        log.info("Renovação de token realizada com sucesso para usuário: {}", email);

        return AuthenticationResponse.of(
                accessToken.token(),
                newRefreshToken.token(),
                ChronoUnit.SECONDS.between(accessToken.issuedAt(), accessToken.expiresAt()),
                user.getEmail(),
                user.getRole().getRoleName());
    }
}
