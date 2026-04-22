package com.practica2.peliculas.service;

import com.practica2.peliculas.model.MovieDTO;
import com.practica2.peliculas.repository.DescarteRepository;
import com.practica2.peliculas.repository.PeliculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecomendadorService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private DescarteRepository descarteRepository;

    // ACTUALIZADO: Ahora recibe los nuevos parámetros de filtro
    public MovieDTO obtenerRecomendacion(String genero, String decada, Double rating, String persona, List<String> excluidasTemporales) {
        try {
            // 1. Limpieza de datos básicos
            String personaLimpia = (persona != null) ? persona.trim() : "";
            String decadaLimpia = (decada != null) ? decada.trim() : "";
            String generoLimpio = (genero != null) ? genero.toLowerCase() : "random";

            // 2. Cargamos exclusiones de la Base de Datos
            List<String> vistas = peliculaRepository.findAll().stream()
                    .map(p -> p.getTitulo()).collect(Collectors.toList());

            List<String> descartadas = descarteRepository.findAll().stream()
                    .map(d -> d.getTitulo()).collect(Collectors.toList());

            List<String> todasExcluidas = new ArrayList<>(vistas);
            todasExcluidas.addAll(descartadas);

            // 3. Añadimos exclusiones temporales de la sesión
            if (excluidasTemporales != null) {
                todasExcluidas.addAll(excluidasTemporales);
            }

            String excluidasStr = String.join(",", todasExcluidas);

            // --- 4. CONSTRUCCIÓN DE LA URL CON FILTROS ---
            // Añadimos los nuevos placeholders: decada, rating y persona
            String url = "http://localhost:5000/api/recomendar?genero={g}&decada={d}&rating={r}&persona={p}&excluir={e}";

            // DEBUG: Para verificar en consola qué enviamos
            System.out.println("DEBUG: Enviando filtros -> Gen: " + generoLimpio + " | Dec: " + decadaLimpia + " | Rat: " + rating + " | Per: " + personaLimpia);

            // 5. Enviamos la petición con todos los parámetros en orden
            return restTemplate.getForObject(url, MovieDTO.class,
                    generoLimpio,
                    decadaLimpia,
                    rating,
                    personaLimpia,
                    excluidasStr
            );

        } catch (HttpStatusCodeException e) {
            System.err.println("Error en la petición a Python: " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            System.err.println("Error genérico: " + e.getMessage());
            return null;
        }
    }
}