package ca.mcgill.ecse321.group1.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// This filter runs once per request, before the controller is invoked.
// It checks for a JWT in the Authorization header and, if valid,
// tells Spring Security who the user is and what role they have.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // Look for "Authorization: Bearer <token>" header
    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      // Strip "Bearer " prefix to get the raw JWT string
      String token = authHeader.substring(7);

      if (jwtUtil.validateToken(token)) {
        String personId = jwtUtil.extractPersonId(token);
        String role = jwtUtil.extractRole(token);

        // Create a Spring Security authentication token.
        // - principal = personId (who the user is)
        // - credentials = null (we don't need a password, the JWT already proved identity)
        // - authorities = ROLE_<role> (e.g. ROLE_Customer, ROLE_Manager)
        //   The "ROLE_" prefix is required by Spring Security's hasRole() checks.
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                personId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));

        // Store the authentication in the SecurityContext so that
        // downstream filters and controllers can access it
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    // Continue the filter chain regardless — if no valid token was found,
    // the request proceeds as unauthenticated (public endpoints still work,
    // protected endpoints will return 401/403)
    filterChain.doFilter(request, response);
  }
}
