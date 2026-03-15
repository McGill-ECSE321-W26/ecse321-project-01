package ca.mcgill.ecse321.group1.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
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
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth
                    // Public endpoints
                    .requestMatchers(HttpMethod.POST, "/api/persons/sessions")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/persons/customers")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/clothing/**")
                    .permitAll()
                    // Error endpoint
                    .requestMatchers("/error")
                    .permitAll()
                    // Manager-only endpoints
                    .requestMatchers(HttpMethod.POST, "/api/persons/employees")
                    .hasRole("Manager")
                    .requestMatchers(HttpMethod.POST, "/api/persons/*/roles/**")
                    .hasRole("Manager")
                    .requestMatchers(HttpMethod.POST, "/api/clothing/**")
                    .hasRole("Manager")
                    .requestMatchers(HttpMethod.PUT, "/api/clothing/**")
                    .hasRole("Manager")
                    .requestMatchers(HttpMethod.DELETE, "/api/clothing/**")
                    .hasRole("Manager")
                    .requestMatchers(HttpMethod.PATCH, "/api/clothing/**")
                    .hasRole("Manager")
                    .requestMatchers(HttpMethod.GET, "/api/persons")
                    .hasRole("Manager")
                    // Cart endpoints - Customer only
                    .requestMatchers("/api/carts/**")
                    .hasRole("Customer")
                    // Order creation - Customer only
                    .requestMatchers(HttpMethod.POST, "/api/orders")
                    .hasRole("Customer")
                    // Order update - Employee or Manager
                    .requestMatchers(HttpMethod.PATCH, "/api/orders/*")
                    .hasAnyRole("Employee", "Manager")
                    // Order listing (no ID) - Employee or Manager
                    .requestMatchers(HttpMethod.GET, "/api/orders")
                    .authenticated()
                    // Order by ID - any authenticated
                    .requestMatchers(HttpMethod.GET, "/api/orders/*")
                    .authenticated()
                    // Person endpoints - any authenticated
                    .requestMatchers(HttpMethod.GET, "/api/persons/*")
                    .authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/persons/*/password")
                    .authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/persons/*/address")
                    .hasRole("Customer")
                    .requestMatchers(HttpMethod.DELETE, "/api/persons/*")
                    .authenticated()
                    // All other requests require authentication
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
