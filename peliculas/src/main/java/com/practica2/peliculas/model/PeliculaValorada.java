package com.practica2.peliculas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class PeliculaValorada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String poster;
    private double estrellas;

    // Añado nuevo el poder guardar el día exacto en el que registramos la película
    private LocalDate fechaGuardado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id") // Creo una columna con el ID del dueño en la tabla de la BD
    private Usuario usuario;

    /**
     * Antes de que el registro se guarde por primera vez
     * en la base de datos, si no le hemos puesto fecha, le asigna de forma
     * automática el día de hoy.
     */
    @PrePersist
    protected void onCreate() {
        if (this.fechaGuardado == null) {
            this.fechaGuardado = LocalDate.now();
        }
    }
}
