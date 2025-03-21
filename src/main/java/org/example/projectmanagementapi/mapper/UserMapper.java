package org.example.projectmanagementapi.mapper;

import org.example.projectmanagementapi.dto.response.UserDto;
import org.example.projectmanagementapi.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
  User toEntity(UserDto dto);

  UserDto toDto(User entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  User partialUpdate(UserDto userDto, @MappingTarget User user);
}
