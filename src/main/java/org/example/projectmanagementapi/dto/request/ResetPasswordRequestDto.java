package org.example.projectmanagementapi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequestDto implements Serializable {
  @NotEmpty(message = "Password should not be empty")
  @Min(value = 8, message = "Password should be at least 8 characters long")
  @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])(?=.*\\d).+$",
      message = "Password must contain at least one capital letter, one symbol, and one number")
  private String password;
}
