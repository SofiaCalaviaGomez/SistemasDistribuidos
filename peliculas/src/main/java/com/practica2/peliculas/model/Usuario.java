package com.practica2.peliculas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El nombre de usuario es obligatorio y no se puede repetir en la BD
    @Column(unique = true, nullable = false)
    private String username;

    // La contraseña es obligatoria (se guardará encriptada gracias al Service)
    @Column(nullable = false)
    private String password;

    // Nombre real del usuario para personalizar la interfaz
    private String nombre;

    // --- MÉTODOS GETTERS Y SETTERS ---
    // Permiten a Spring Boot leer y escribir datos en los campos privados

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

