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

    // Dirección de correo electrónico obligatoria y única para el envío de alertas/activación
    @Column(unique = true, nullable = false)
    private String email;

    // La contraseña es obligatoria (se guardará encriptada gracias al Service)
    @Column(nullable = false)
    private String password;

    // Nombre real del usuario para personalizar la interfaz
    private String nombre;

    // Controla si el usuario ha verificado su cuenta (forzamos valor por defecto en BD)
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean activo = false;

    // Almacena el código secreto temporal enviado por email para validar la cuenta
    private String tokenActivacion;

    // --- MÉTODOS GETTERS Y SETTERS ---
    // Permiten a Spring Boot leer y escribir datos en los campos privados

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getTokenActivacion() { return tokenActivacion; }
    public void setTokenActivacion(String tokenActivacion) { this.tokenActivacion = tokenActivacion; }
}
