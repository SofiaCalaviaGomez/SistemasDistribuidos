package com.practica2.peliculas.repository;

import com.practica2.peliculas.model.PeliculaValorada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeliculaRepository extends JpaRepository<PeliculaValorada, Long> {
    List<PeliculaValorada> findTop4ByOrderByIdDesc();
}
