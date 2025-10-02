package dias.heimy.dto.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import dias.heimy.domain.entity.User;
import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface UserMapper {

    UserResponse toResponse(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isSystemAdmin", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    User toEntity(UserRegisterRequest domain);
}
