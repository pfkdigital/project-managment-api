package org.example.projectmanagementapi.dto;

import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.Role;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public record UserDto(Integer id, String username, String email, String password, Role role,
                      String displayImageUrl) implements Serializable {
}