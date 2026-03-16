package ca.mcgill.ecse321.group1.security;

import ca.mcgill.ecse321.group1.model.Person;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Utility class for creating and validating JWT tokens.
// Tokens contain the person's ID, email, and role, and are signed with HMAC-SHA256.
@Component
public class JwtUtil {

  private final SecretKey key;

  // Decode the base64 secret from application.properties and create an HMAC signing key
  public JwtUtil(@Value("${jwt.secret}") String secret) {
    this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
  }

  // Build a JWT with the person's ID as the subject, plus email and role claims.
  // Tokens expire after 365 days (NFR3: no session timeout, so we use a long-lived token).
  public String generateToken(Person person, String role) {
    return Jwts.builder()
        .subject(person.getPersonID())
        .claim("email", person.getEmail())
        .claim("role", role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000))
        .signWith(key)
        .compact();
  }

  // Extract the person ID (stored as the JWT "subject" claim)
  public String extractPersonId(String token) {
    return parseClaims(token).getSubject();
  }

  // Extract the role (stored as a custom "role" claim)
  public String extractRole(String token) {
    return parseClaims(token).get("role", String.class);
  }

  // Return true if the token's signature is valid and not expired.
  // Any parsing/validation failure (bad signature, expired, malformed) returns false.
  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  // Verify the token signature and parse out the claims payload
  private Claims parseClaims(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }
}
