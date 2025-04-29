package org.example.projectmanagementapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordDto implements Serializable {
  @NotEmpty(message = "Email should not be empty")
  @Email(message = "Email should be valid", regexp = "^(.+)@(.+)$")
  private String email;
}
