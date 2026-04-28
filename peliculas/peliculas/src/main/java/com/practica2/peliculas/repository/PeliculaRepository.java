package com.practica2.peliculas.repository;

import com.practica2.peliculas.model.PeliculaValorada;
import com.practica2.peliculas.model.Usuario; // Necesitas importar el modelo Usuario
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeliculaRepository extends JpaRepository<PeliculaValorada, Long> {

    /**
     * Busca TODAS las películas de un usuario específico.
     * Útil para la página de "Mi Historial" completo.
     */
    List<PeliculaValorada> findByUsuario(Usuario usuario);

    /**
     * Busca las últimas 4 películas de un usuario específico.
     * Útil para el resumen de la página principal.
     */
    List<PeliculaValorada> findTop4ByUsuarioOrderByIdDesc(Usuario usuario);
}