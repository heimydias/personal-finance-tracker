package dias.heimy.service;

import dias.heimy.dto.request.LoginRequest;
import dias.heimy.dto.request.RefreshTokenRequest;
import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.response.AuthenticationResponse;
import dias.heimy.dto.response.UserResponse;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {
    @Transactional
    UserResponse register(UserRegisterRequest request);

    @Transactional
    AuthenticationResponse login(LoginRequest request);

    @Transactional
    AuthenticationResponse refreshToken(RefreshTokenRequest request);
}
