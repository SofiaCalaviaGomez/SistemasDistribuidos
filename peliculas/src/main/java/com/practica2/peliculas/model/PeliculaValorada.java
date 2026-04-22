package com.practica2.peliculas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PeliculaValorada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String poster;
    private double estrellas; // Del 1 al 5
}
