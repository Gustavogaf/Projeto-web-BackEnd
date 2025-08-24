package com.example.demo.config;

import com.example.demo.Service.AuthUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthUserDetailsService authUserDetailsService;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(authUserDetailsService)
                .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Endpoints Públicos
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/esportes/**", "/api/cursos/**", "/api/torneios/**",
                                "/api/partidas/**")
                        .permitAll()

                        // Permite que qualquer usuário autenticado veja os técnicos
                        .requestMatchers(HttpMethod.GET, "/api/tecnicos", "/api/tecnicos/**").authenticated()

                        // Endpoints de ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Endpoints de COORDENADOR
                        .requestMatchers("/api/coordenadores/**").hasRole("COORDENADOR")

                        // Endpoints de TECNICO
                        .requestMatchers("/api/tecnicos/**").hasRole("TECNICO")

                        // Endpoints de ARBITRO
                        .requestMatchers("/api/arbitros/**").hasRole("ARBITRO")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
