package com.practica2.peliculas.service;

import com.practica2.peliculas.model.Usuario;
import com.practica2.peliculas.repository.UsuarioRepository;
import com.practica2.peliculas.repository.PeliculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository; // Conexión con la tabla de usuarios

    @Autowired
    private PeliculaRepository peliculaRepository; // Conexión con tu repositorio de valoraciones/historial

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // Herramienta para cifrar contraseñas

    /**
     * Método para dar de alta a un nuevo usuario.
     * Recibe un objeto 'usuario' con los datos del formulario.
     */
    public void registrar(Usuario usuario) {
        // Cifra la contraseña aquí, justo antes de persistir el objeto garantizando que nunca se guarde en texto plano en PostgreSQL.
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Guarda el usuario de forma segura en la base de datos.
        usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por su token de activación, habilita su cuenta
     * y destruye el token para que no pueda volver a ser reutilizado.
     * Retorna true si la activación fue exitosa.
     */
    public boolean activarPorToken(String token) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByTokenActivacion(token);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(true);              // Activa la cuenta
            usuario.setTokenActivacion(null);     // Borra el token para invalidarlo
            usuarioRepository.save(usuario);      // Actualizaen la BD
            return true;
        }

        return false; // Token no válido o expirado
    }

    /**
     * Actualizo los datos del perfil de un usuario existente.
     * Controlo si se ha editado el nombre o si se desea cambiar la contraseña.
     */
    public void actualizar(Usuario usuarioActual, String nuevoNombre, String nuevoPassword) {
        // 1. Si ha introducido un nombre real válido y distinto, se va actualizar
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            usuarioActual.setNombre(nuevoNombre.trim());
        }

        // 2. Si el input de contraseña no está vacío, significa que quiere cambiarla.
        // Se encripta con BCrypt antes de impactar en la base de datos.
        if (nuevoPassword != null && !nuevoPassword.trim().isEmpty()) {
            usuarioActual.setPassword(passwordEncoder.encode(nuevoPassword.trim()));
        }

        usuarioRepository.save(usuarioActual);
    }

    /**
     * Elimino permanentemente la cuenta de un usuario de la base de datos.
     * Usamos @Transactional para que ambos borrados se ejecuten de forma segura como un bloque único.
     */
    @Transactional
    public void eliminar(Usuario usuario) {
        // 3. Limpio primero todas las valoraciones que apuntan a este usuario en tu repositorio real
        peliculaRepository.deleteByUsuario(usuario);

        // 4. Ahora que la base de datos está limpia de dependencias, elimino al usuario
        usuarioRepository.delete(usuario);
    }
}