package com.practica2.peliculas.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MovieDTO {
    @JsonProperty("title") // Esto le dice: "Si de Python viene 'title', guárdalo en 'titulo'"
    private String titulo;

    @JsonProperty("year")
    private String anio;

    private String poster; // Este no cambia porque es igual en ambos

    @JsonProperty("plot")
    private String descripcion;

    // --- GETTERS Y SETTERS ---
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAnio() { return anio; }
    public void setAnio(String anio) { this.anio = anio; }

    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public MovieDTO() {}
}
