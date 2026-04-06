package ca.mcgill.ecse321.group1.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

// Configures Spring Security for stateless JWT authentication
// - CSRF is disabled because we use stateless tokens (no cookies/sessions to protect)
// - Sessions are disabled (STATELESS) every request must carry its own JWT
@Configuration
@EnableMethodSecurity
@Profile("!seed")
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        // CORS CONFIG
        .cors(
            cors ->
                cors.configurationSource(
                    request -> {
                      CorsConfiguration config = new CorsConfiguration();
                      config.addAllowedOriginPattern("*");
                      config.addAllowedMethod("*");
                      config.addAllowedHeader("*");
                      config.setAllowCredentials(true);
                      return config;
                    }))
        // No server-side sessions authentication comes from the JWT on each request
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // URL-based authorization rules.
        // Rules are evaluated top-to-bottom; the first match wins.
        .authorizeHttpRequests(
            auth ->
                auth
                    // --- Public endpoints (no token needed) ---
                    // Login and customer signup must be accessible without a token
                    .requestMatchers(HttpMethod.POST, "/api/persons/sessions")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/persons/customers")
                    .permitAll()
                    // Anyone can browse the catalog (GET only)
                    .requestMatchers(HttpMethod.GET, "/api/clothing/**")
                    .permitAll()
                    // Spring Boot's default error endpoint
                    .requestMatchers("/error")
                    .permitAll()

                    // --- Manager-only endpoints ---
                    // Only managers can create employee accounts
                    .requestMatchers(HttpMethod.POST, "/api/persons/employees")
                    .hasRole("Manager")
                    // Only managers can add roles to existing users
                    .requestMatchers(HttpMethod.POST, "/api/persons/*/roles/**")
                    .hasRole("Manager")
                    // Only managers can modify the clothing catalog (create, update, delete)
                    .requestMatchers(HttpMethod.POST, "/api/clothing/**")
                    .hasRole("Manager")
                    .requestMatchers(HttpMethod.PUT, "/api/clothing/**")
                    .hasRole("Manager")
                    .requestMatchers(HttpMethod.DELETE, "/api/clothing/**")
                    .hasRole("Manager")
                    .requestMatchers(HttpMethod.PATCH, "/api/clothing/**")
                    .hasRole("Manager")
                    // Only managers can list employees or customers
                    .requestMatchers(HttpMethod.GET, "/api/persons/employees")
                    .hasRole("Manager")
                    .requestMatchers(HttpMethod.GET, "/api/persons/customers")
                    .hasRole("Manager")
                    // Customers can fetch their own profile by customer ID
                    .requestMatchers(HttpMethod.GET, "/api/persons/customers/*")
                    .hasRole("Customer")

                    // --- Customer-only endpoints ---
                    // All cart operations require Customer role
                    .requestMatchers("/api/carts/**")
                    .hasRole("Customer")
                    // Only customers can place new orders
                    .requestMatchers(HttpMethod.POST, "/api/orders")
                    .hasRole("Customer")

                    // --- Employee, Manager, or Customer endpoints ---
                    // Updating orders (assign employee, change status/date) requires Employee or
                    // Manager; customers can also update their own orders (delivery date, cancel)

                    // CURRENTLY ADDED CUSTOMERS TO PATCH TO CANCEL/MODIFY DELIVERY DATE
                    // THIS WILL NEED TO BE LOOKED AT AGAIN AND BE CHANGED FOR SECURITY REASONS
                    .requestMatchers(HttpMethod.PATCH, "/api/orders/*")
                    .hasAnyRole("Employee", "Manager", "Customer")

                    // --- Any authenticated user ---
                    // Viewing orders requires login but any role can do it
                    .requestMatchers(HttpMethod.GET, "/api/orders")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/orders/*")
                    .authenticated()
                    // Modifying own profile
                    .requestMatchers(HttpMethod.PATCH, "/api/persons/*/password")
                    .authenticated()
                    // Only customers can update their address
                    .requestMatchers(HttpMethod.PATCH, "/api/persons/*/address")
                    .hasRole("Customer")
                    .requestMatchers(HttpMethod.DELETE, "/api/persons/*")
                    .authenticated()

                    // Everything else requires authentication
                    .anyRequest()
                    .authenticated())
        // Insert our JWT filter before Spring's default username/password filter
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
