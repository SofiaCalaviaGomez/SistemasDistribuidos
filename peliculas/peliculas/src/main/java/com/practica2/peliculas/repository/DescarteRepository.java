package com.practica2.peliculas.repository;

import com.practica2.peliculas.model.PeliculaDescartada;
import org.springframework.data.jpa.repository.JpaRepository;

// Interfaz que extiende de JpaRepository para obtener operaciones de base de datos automáticas
// Se le indica la Entidad (PeliculaDescartada) y el tipo del ID (Long)
public interface DescarteRepository extends JpaRepository<PeliculaDescartada, Long> {}
