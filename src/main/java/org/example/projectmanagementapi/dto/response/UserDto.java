package org.example.projectmanagementapi.dto.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projectmanagementapi.enums.Role;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto implements Serializable {
  private Integer id;
  private String username;
  private String email;
  private Role role;
  private String displayImageUrl;
}
