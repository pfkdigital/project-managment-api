package org.example.projectmanagementapi.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class VerificationTokenUtility {

  public String generateVerificationToken() {
    Random random = new Random();
    char[] token = new char[6];
    for (int i = 0; i < 6; i++) {
      token[i] = (char) (random.nextInt(10) + '0');
    }
    return new String(token);
  }
}
