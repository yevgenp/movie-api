package com.example.movieapi.config;

import static org.springframework.http.HttpMethod.GET;

import com.example.movieapi.controller.EpisodesController;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@RequiredArgsConstructor
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

  @Bean
  public MapReactiveUserDetailsService userDetailsService() {
    UserDetails user1 = User.withUsername("user1")
        .password(encoder().encode("user1"))
        .roles("USER")
        .build();
    UserDetails user2 = User.withUsername("user2")
        .password(encoder().encode("user2"))
        .roles("USER")
        .build();
    return new MapReactiveUserDetailsService(user1, user2);
  }

  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityWebFilterChain configureAccess(ServerHttpSecurity http) throws Exception {
    return http
        .httpBasic()
        .and()
        .csrf().disable()
        .authorizeExchange()
        .pathMatchers(GET, EpisodesController.PATH).permitAll()
        .anyExchange().authenticated()
        .and()
        .build();
  }
}