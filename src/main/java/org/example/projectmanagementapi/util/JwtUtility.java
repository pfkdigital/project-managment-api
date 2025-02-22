package org.example.projectmanagementapi.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtility {

  @Value("${security.jwt.secret.key}")
  private String jwtSecret;

  @Value("${security.jwt.expiration.time}")
  private int jwtExpirationMs;

  @Value("${security.jwt.refresh.expiration.time}")
  private int jwtRefreshExpirationMs;

  public Claims extract(String token) {
    return (Claims) Jwts.parser().verifyWith(getSignInKey()).build().parse(token).getPayload();
  }

  public String extractUsername(String token) {
    return extract(token).getSubject();
  }

  public Date extractExpiration(String token) {
    return extract(token).getExpiration();
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date(System.currentTimeMillis()));
  }

  public String generateAccessToken(UserDetails userDetails) {
    return buildToken(
        userDetails, new Date(System.currentTimeMillis() + jwtExpirationMs), Map.of());
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(
        userDetails, new Date(System.currentTimeMillis() + jwtRefreshExpirationMs), Map.of());
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    var username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private SecretKey getSignInKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  private String buildToken(
      UserDetails userDetails, Date expirationTime, Map<String, Object> claims) {
    var authorities = userDetails.getAuthorities();

    return Jwts.builder()
        .claims(claims)
        .claim("authorities", authorities)
        .subject(userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(expirationTime)
        .signWith(getSignInKey())
        .compact();
  }
}
