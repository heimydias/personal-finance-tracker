package dias.heimy.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import dias.heimy.domain.enums.UserRole;
import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.response.UserResponse;
import dias.heimy.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for UserControllerImpl")
class UserControllerImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserControllerImpl userController;

    @Test
    @DisplayName("Should create user successfully when valid data provided")
    void shouldCreateUser_WhenValidData() {

        var request = new UserRegisterRequest("test@example.com", "password123", UserRole.USER);
        var expectedResponse = new UserResponse(
                "test-id",
                "test@example.com",
                UserRole.USER,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "SYSTEM",
                "SYSTEM");

        when(userService.createUser(any(UserRegisterRequest.class), anyString()))
                .thenReturn(expectedResponse);

        var result = userController.createUser(request, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
    }
}
