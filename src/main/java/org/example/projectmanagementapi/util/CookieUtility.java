package org.example.projectmanagementapi.util;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtility {

  @Value("${security.jwt.expiration.time}")
  private int accessTokenExpiration;

  @Value("${security.jwt.refresh.expiration.time}")
  private int refreshTokenExpiration;

  public Cookie createTokenCookie(String token, String label) {
    Cookie cookie = new Cookie(label, token);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setMaxAge(label.equals("access_token") ? accessTokenExpiration : refreshTokenExpiration);
    return cookie;
  }

  public void clearCookies(Cookie[] cookies) {
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("access_token") || cookie.getName().equals("refresh_token")) {
        cookie.setValue("");
        cookie.setMaxAge(0);
        cookie.setPath("/");
      }
    }
  }
}
