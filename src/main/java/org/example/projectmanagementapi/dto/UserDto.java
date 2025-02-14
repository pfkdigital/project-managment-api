package org.example.projectmanagementapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.Role;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private Integer id;
    private String username;
    private String email;
    private String password;
    private Role role;
    private String displayImageUrl;
}