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

    private final RestTemplate restTemplate = new RestTemplate(); // Herramienta para hacer peticiones HTTP a Python

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private DescarteRepository descarteRepository;

    // Método principal que solicita una película al API de Flask (Python)
    public MovieDTO obtenerRecomendacion(String genero, String decada, Double rating, String persona, List<String> excluidasTemporales) {
        try {
            // Limpieza y preparación de filtros (evita enviar valores nulos)
            String personaLimpia = (persona != null) ? persona.trim() : "";
            String decadaLimpia = (decada != null) ? decada.trim() : "";
            String generoLimpio = (genero != null) ? genero.toLowerCase() : "random";

            // Buscaa qué películas NO debemos mostrar
            // Saca los títulos de la tabla de 'Valoradas' y de 'Descartadas'
            List<String> vistas = peliculaRepository.findAll().stream()
                    .map(p -> p.getTitulo()).collect(Collectors.toList());

            List<String> descartadas = descarteRepository.findAll().stream()
                    .map(d -> d.getTitulo()).collect(Collectors.toList());

            List<String> todasExcluidas = new ArrayList<>(vistas);
            todasExcluidas.addAll(descartadas);

            // Se Añaden las películas que el usuario ha saltado en esta sesión actual
            if (excluidasTemporales != null) {
                todasExcluidas.addAll(excluidasTemporales);
            }

            // Convierte la lista de títulos en una sola cadena separada por comas para enviarla por URL
            String excluidasStr = String.join(",", todasExcluidas);

            // Comunicación con el API de terceros (Python)
            // Defino la dirección del servidor Flask y sus parámetros
            String url = "http://localhost:5000/api/recomendar?genero={g}&decada={d}&rating={r}&persona={p}&excluir={e}";

            // LOG de control para depurar errores en la consola de IntelliJ
            System.out.println("DEBUG: Enviando filtros -> Gen: " + generoLimpio + " | Dec: " + decadaLimpia + " | Rat: " + rating + " | Per: " + personaLimpia);

            //Java espera a que Python responda con un objeto MovieDTO
            return restTemplate.getForObject(url, MovieDTO.class,
                    generoLimpio, decadaLimpia, rating, personaLimpia, excluidasStr
            );

        } catch (HttpStatusCodeException e) {
            // Captura errores específicos de HTTP (ej: Python devolvió 404 o 500)
            System.err.println("Error en la petición a Python: " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            // Captura cualquier otro fallo (ej: Python está apagado)
            System.err.println("Error genérico: " + e.getMessage());
            return null;
        }
    }
}