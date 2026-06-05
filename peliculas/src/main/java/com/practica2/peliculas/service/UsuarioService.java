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
        // SEGURIDAD: Nunca guardo la contraseña tal cual la escribe el usuario.
        // La transformo en un código cifrado (hash) mediante BCrypt.
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        //Una vez protegida la clave, guardo el usuario en H2.
        usuarioRepository.save(usuario);
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

    //Elimino permanentemente la cuenta de un usuario de la base de datos.
    public void eliminar(Usuario usuario) {
        usuarioRepository.delete(usuario);
    }
}
