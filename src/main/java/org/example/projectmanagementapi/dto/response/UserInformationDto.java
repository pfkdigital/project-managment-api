package org.example.projectmanagementapi.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projectmanagementapi.enums.Role;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserInformationDto {
  private String username;
  private String email;
  private Role role;
  private String displayImageUrl;
}
