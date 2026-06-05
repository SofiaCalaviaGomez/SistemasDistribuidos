package com.practica2.peliculas.service;

import com.practica2.peliculas.model.Usuario;
import com.practica2.peliculas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository; // Acceso a la base de datos de usuarios

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busco al usuario en la base de datos por su nombre de usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Si lo encuentra, lo convierte en un objeto "UserDetails" que Spring Security entiende.
        // Usamos el constructor .disabled() -> Si el usuario NO está activo (!usuario.isActivo()),
        // pasará a estar deshabilitado y se rechazará el inicio de sesión de forma segura.
        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .disabled(!usuario.isActivo())
                .authorities(new ArrayList<>()) // Mantiene la lista vacía de roles/permisos
                .build();
    }
}