package org.example.projectmanagementapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegisterRequestDto implements Serializable {
  @Email(message = "Email should be valid", regexp = "^(.+)@(.+)$")
  @NotEmpty(message = "Email should not be empty")
  private String email;

  @NotEmpty(message = "Username should not be empty")
  private String username;

  @NotEmpty(message = "Password should not be empty")
  @Min(value = 8, message = "Password should be at least 8 characters long")
  @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])(?=.*\\d).+$",
      message = "Password must contain at least one capital letter, one symbol, and one number")
  private String password;
}
