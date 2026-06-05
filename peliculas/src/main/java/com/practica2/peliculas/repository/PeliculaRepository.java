package com.practica2.peliculas.repository;

import com.practica2.peliculas.model.PeliculaValorada;
import com.practica2.peliculas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PeliculaRepository extends JpaRepository<PeliculaValorada, Long> {

    //Busca TODAS las películas de un usuario específico
    List<PeliculaValorada> findByUsuario(Usuario usuario);

    //Busca las últimas 4 películas de un usuario específico
    List<PeliculaValorada> findTop4ByUsuarioOrderByIdDesc(Usuario usuario);

    // Le dice a Spring Data JPA que genere el "DELETE FROM pelicula_valorada WHERE usuario_id = ?"
    void deleteByUsuario(Usuario usuario);
}