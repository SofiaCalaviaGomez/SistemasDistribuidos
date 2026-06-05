package com.practica2.peliculas.repository;

import com.practica2.peliculas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Interfaz para gestionar la tabla de Usuarios en la base de datos
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /**
     * Busca un usuario por su apodo (username).
     * Retorna un 'Optional' para evitar errores de puntero nulo (NullPointerException)
     * si el usuario no existe.
     */
    Optional<Usuario> findByUsername(String username);

    //Busca un usuario utilizando el token de activación.
    Optional<Usuario> findByTokenActivacion(String tokenActivacion);
}