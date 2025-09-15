package dias.heimy.controller.impl;

import dias.heimy.controller.AuthController;
import dias.heimy.dto.request.LoginRequest;
import dias.heimy.dto.request.RefreshTokenRequest;
import dias.heimy.dto.response.AuthenticationResponse;
import dias.heimy.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthenticationResponse> token(LoginRequest request) {
        log.info("Requisição de token recebida para email: {}", request.email());
        AuthenticationResponse response = authService.login(request);
        log.info("Token gerado com sucesso para email: {}", request.email());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AuthenticationResponse> refreshToken(RefreshTokenRequest request) {
        log.info("Requisição de renovação de token recebida");
        AuthenticationResponse response = authService.refreshToken(request);
        log.info("Token renovado com sucesso");
        return ResponseEntity.ok(response);
    }
}
