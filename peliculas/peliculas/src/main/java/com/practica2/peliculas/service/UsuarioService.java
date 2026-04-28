package com.practica2.peliculas.service;

import com.practica2.peliculas.model.Usuario;
import com.practica2.peliculas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository; // Conexión con la tabla de usuarios

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // Herramienta para cifrar contraseñas

    /**
     * Método para dar de alta a un nuevo usuario.
     * Recibe un objeto 'usuario' con los datos del formulario.
     */
    public void registrar(Usuario usuario) {
        // SEGURIDAD: Nunca guardamos la contraseña tal cual la escribe el usuario.
        // La transformamos en un código cifrado (hash) mediante BCrypt.
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // PERSISTENCIA: Una vez protegida la clave, guardamos el usuario en H2.
        usuarioRepository.save(usuario);
    }
}
