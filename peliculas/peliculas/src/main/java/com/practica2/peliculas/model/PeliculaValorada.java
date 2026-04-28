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
    private double estrellas;
    
    // Muchos registros de películas pueden pertenecer a UN solo usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id") // Crea una columna con el ID del dueño en la tabla de la BD
    private Usuario usuario;
}
