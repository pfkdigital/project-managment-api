package org.example.projectmanagementapi.util;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class TokenUtility {

  public String generateToken() {
    Random random = new Random();
    char[] token = new char[6];
    for (int i = 0; i < 6; i++) {
      token[i] = (char) (random.nextInt(10) + '0');
    }
    return new String(token);
  }
}
