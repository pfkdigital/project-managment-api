package org.example.projectmanagementapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequestDto {

  @NotEmpty(message = "Username should not be empty")
  private String username;

  @NotEmpty(message = "Password should not be empty")
  private String password;
}
