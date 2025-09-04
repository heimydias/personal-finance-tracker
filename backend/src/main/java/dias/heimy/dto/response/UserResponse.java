package dias.heimy.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dias.heimy.domain.enums.UserRole;
import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String email,
        UserRole role,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime updatedAt) {}
