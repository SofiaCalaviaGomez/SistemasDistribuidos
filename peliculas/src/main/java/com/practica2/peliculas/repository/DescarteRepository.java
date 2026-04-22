package com.practica2.peliculas.repository;

import com.practica2.peliculas.model.PeliculaDescartada;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DescarteRepository extends JpaRepository<PeliculaDescartada, Long> {}
