package com.example.demoinitial.config;

import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    @Order(0)
    SecurityFilterChain resources(HttpSecurity http) throws Exception {
        String[] permittedResources = new String[] {
                "/", "/static/**","/css/**","/js/**","/webfonts/**", "/webjars/**",
                "/index.html","/favicon.ico", "/error",
                "/v3/**","/swagger-ui.html","/swagger-ui/**", "/actuator/**"
        };
        http
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(permittedResources)
                .authorizeHttpRequests((
                                               authorize) -> authorize.anyRequest().permitAll()
                )
                .requestCache(RequestCacheConfigurer::disable)
                .securityContext(SecurityContextConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    @Order(1)
    SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(OPTIONS).permitAll()
                        .anyRequest().hasAnyRole("USER", "MODERATOR", "ADMIN")
                )
                .httpBasic(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    @Order(2)
    protected SecurityFilterChain mvcFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(OPTIONS).permitAll()
                        .requestMatchers("/", "/index.html", "/login", "/error").permitAll()
                        .requestMatchers("/users/**").hasAnyRole("USER", "MODERATOR", "ADMIN")
                        .requestMatchers("/stomp-broadcast/**").hasAnyRole("USER", "MODERATOR", "ADMIN")
                        .requestMatchers("/broadcast/**").hasAnyRole("USER", "MODERATOR", "ADMIN")
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest()
                        .authenticated()
                );

        http
                .formLogin(login -> login.loginPage("/login").permitAll())
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/"));

        http.headers(headers ->
                             headers.frameOptions(FrameOptionsConfig::sameOrigin));

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user")
                               .password(passwordEncoder().encode("user"))
                               .roles("USER").build());
        manager.createUser(User.withUsername("admin")
                               .password(passwordEncoder().encode("admin"))
                               .roles("ADMIN", "USER").build());
        manager.createUser(User.withUsername("admin@example.com")
                               .password(passwordEncoder().encode("admin"))
                               .roles("ADMIN", "USER").build());
        manager.createUser(User.withUsername("admin@admin.ch")
                               .password(passwordEncoder().encode("admin"))
                               .roles("ADMIN", "USER").build());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
