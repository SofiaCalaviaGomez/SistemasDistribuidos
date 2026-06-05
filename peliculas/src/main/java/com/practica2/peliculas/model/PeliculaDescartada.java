package com.practica2.peliculas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PeliculaDescartada {
    @Id // Define que el siguiente campo es la clave primaria (ID único)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // El ID se genera solo (1, 2, 3...)
    private Long id;

    private String titulo; // Nombre de la película descartada

    // Constructor vacío: Obligatorio para que Hibernate funcione correctamente
    public PeliculaDescartada() {}

    // Constructor práctico: Para crear el objeto rápidamente pasando solo el título
    public PeliculaDescartada(String titulo) {
        this.titulo = titulo;
    }

    // --- MÉTODOS GETTERS Y SETTERS ---
    // hacen que el Spring pueda leer y escribir datos en los atributos privados

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
}
