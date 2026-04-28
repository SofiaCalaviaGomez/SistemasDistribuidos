package com.practica2.peliculas.config;

import com.practica2.peliculas.model.Usuario;
import com.practica2.peliculas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Define el algoritmo de encriptación (BCrypt) para las contraseñas
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura la cadena de filtros de seguridad (quién entra y quién no)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Permite que la consola de H2 se vea correctamente en marcos (frames)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                // DESACTIVAR CSRF: Necesario para que los formularios POST (como el de registro)
                // funcionen sin tokens de seguridad durante el desarrollo.
                .csrf(csrf -> csrf.disable())

                // REGLAS DE ACCESO:
                .authorizeHttpRequests(auth -> auth
                        // Estas rutas son públicas (cualquiera puede verlas sin loguearse)
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/login")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/registro")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/css/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/js/**")).permitAll()

                        // Cualquier otra ruta requiere que el usuario esté autenticado
                        .anyRequest().authenticated()
                )

                // CONFIGURACIÓN DEL LOGIN:
                .formLogin(login -> login
                        .loginPage("/login") // Nuestra página personalizada de login
                        .defaultSuccessUrl("/principal", true) // A dónde va el usuario al entrar con éxito
                        .permitAll()
                )

                // CONFIGURACIÓN DEL LOGOUT (Cerrar sesión):
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout") // Redirige al login tras salir
                        .permitAll()
                );

        return http.build();
    }

    /**
     * NOTA: Esta clase UsuarioService debería estar en su propio archivo .java
     * para seguir las buenas prácticas de Spring, pero aquí funciona como clase interna.
     */
    @Service
    public class UsuarioService {

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private BCryptPasswordEncoder passwordEncoder;

        // Método para guardar un nuevo usuario
        public void registrar(Usuario usuario) {
            // 1. Encriptamos la contraseña por seguridad (nunca guardar en texto plano)
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

            // 2. Persistencia: se guarda el objeto en la base de datos H2
            usuarioRepository.save(usuario);
        }
    }
}