package dias.heimy.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dias.heimy.domain.enums.UserRole;
import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.request.UserUpdateRequest;
import dias.heimy.dto.response.PageResponse;
import dias.heimy.dto.response.UserResponse;
import dias.heimy.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
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

    @Test
    @DisplayName("Should list users successfully with pagination headers")
    void shouldListUsers_WhenValidPaginationHeaders() {

        var userResponse = createUserResponse();
        var pageResponse = PageResponse.of(0, 10, 1, List.of(userResponse));

        when(userService.listUsers(any(Pageable.class), anyString())).thenReturn(pageResponse);

        var result = userController.listUsers(0, 10, "email", "asc", "Bearer admin_token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(pageResponse);
        Assertions.assertNotNull(result.getBody());
        assertThat(result.getBody().content()).hasSize(1);
        assertThat(result.getBody().pageNumber()).isZero();
        assertThat(result.getBody().pageSize()).isEqualTo(10);
        verify(userService).listUsers(any(Pageable.class), eq("Bearer admin_token"));
    }

    @Test
    @DisplayName("Should list users without sorting when sort parameter is null")
    void shouldListUsers_WhenNoSortParameter() {

        var pageResponse = PageResponse.<UserResponse>of(0, 10, 0, List.of());

        when(userService.listUsers(any(Pageable.class), anyString())).thenReturn(pageResponse);

        var result = userController.listUsers(0, 10, null, "asc", "Bearer admin_token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(pageResponse);
        verify(userService).listUsers(any(Pageable.class), eq("Bearer admin_token"));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserById_WhenValidId() {

        var userId = UUID.randomUUID();
        var userResponse = createUserResponse();

        when(userService.getUserById(userId, "Bearer token")).thenReturn(userResponse);

        var result = userController.getUserById(userId, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(userResponse);
        verify(userService).getUserById(userId, "Bearer token");
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUser_WhenValidData() {

        var userId = UUID.randomUUID();
        var updateRequest = new UserUpdateRequest("newemail@example.com", "newpassword", UserRole.USER);
        var updatedResponse = createUserResponse();

        when(userService.updateUser(userId, updateRequest, "Bearer token")).thenReturn(updatedResponse);

        var result = userController.updateUser(userId, updateRequest, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
        verify(userService).updateUser(userId, updateRequest, "Bearer token");
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUser_WhenValidId() {

        var userId = UUID.randomUUID();

        doNothing().when(userService).deleteUser(userId, "Bearer token");

        var result = userController.deleteUser(userId, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
        verify(userService).deleteUser(userId, "Bearer token");
    }

    private UserResponse createUserResponse() {
        return new UserResponse(
                "test-id",
                "test@example.com",
                UserRole.USER,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "SYSTEM",
                "SYSTEM");
    }
}
