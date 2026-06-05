package com.practica2.peliculas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Defino el algoritmo de encriptación (BCrypt) para las contraseñas
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuro la cadena de filtros de seguridad (quién entra y quién no)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // La consola de H2 se vea correctamente en marcos (frames)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                // DESACTIVAR CSRF
                .csrf(csrf -> csrf.disable())

                // REGLAS DE ACCESO:
                .authorizeHttpRequests(auth -> auth
                        // Estas rutas son públicas (acceso libre)
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/login")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/registro")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/css/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/js/**")).permitAll()

                        // Protejo explícitamente las rutas del perfil (requieren login)
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/perfil/**")).authenticated()

                        // Cualquier otra ruta requiere que el usuario esté autenticado
                        .anyRequest().authenticated()
                )

                // CONFIGURACIÓN DEL LOGIN:
                .formLogin(login -> login
                        .loginPage("/login") // Nuestra página personalizada de login
                        .defaultSuccessUrl("/principal", true) // Redirección tras éxito
                        .permitAll()
                )

                // CONFIGURACIÓN DEL LOGOUT (Cerrar sesión):
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout") // Redirige al login tras salir
                        .invalidateHttpSession(true)       // Destruye la sesión de seguridad
                        .clearAuthentication(true)         // Borra las credenciales en memoria
                        .permitAll()
                );

        return http.build();
    }
}