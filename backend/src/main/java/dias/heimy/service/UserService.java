package dias.heimy.service;

import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.request.UserUpdateRequest;
import dias.heimy.dto.response.UserResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse createUser(UserRegisterRequest request, String authorizationHeader);

    Page<UserResponse> listUsers(Pageable pageable, String authorizationHeader);

    UserResponse getUserById(UUID id, String authorizationHeader);

    UserResponse updateUser(UUID id, UserUpdateRequest request, String authorizationHeader);

    void deleteUser(UUID id, String authorizationHeader);
}
