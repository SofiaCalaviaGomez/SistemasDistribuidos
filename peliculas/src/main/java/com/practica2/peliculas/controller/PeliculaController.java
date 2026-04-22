package com.practica2.peliculas.controller;

import com.practica2.peliculas.model.MovieDTO;
import com.practica2.peliculas.model.PeliculaDescartada;
import com.practica2.peliculas.model.PeliculaValorada;
import com.practica2.peliculas.repository.DescarteRepository;
import com.practica2.peliculas.repository.PeliculaRepository;
import com.practica2.peliculas.service.RecomendadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PeliculaController {

    @Autowired
    private RecomendadorService recomendadorService;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private DescarteRepository descarteRepository;

    // --- PANTALLA PRINCIPAL ---
    @GetMapping("/principal")
    public String principal(Model model) {
        model.addAttribute("recientes", peliculaRepository.findTop4ByOrderByIdDesc());
        return "principal";
    }

    // --- OBTENER RECOMENDACIÓN CON FILTROS ---
    @PostMapping("/recomendar")
    public String recomendar(@RequestParam String genero,
                             @RequestParam(required = false) String decada,
                             @RequestParam(required = false, defaultValue = "0") Double rating,
                             @RequestParam(required = false) String persona,
                             @RequestParam(required = false) String excluirTemporal,
                             HttpSession session,
                             Model model) {

        // 1. Mantener las pelis recientes visibles
        model.addAttribute("recientes", peliculaRepository.findTop4ByOrderByIdDesc());

        // 2. DEVOLVER FILTROS A LA VISTA (Para que no se borren en el HTML)
        model.addAttribute("generoSeleccionado", genero);
        model.addAttribute("decadaSeleccionada", decada);
        model.addAttribute("ratingSeleccionado", rating);
        model.addAttribute("personaSeleccionada", persona); // <-- Importante para el buscador de texto

        // 3. Gestionar historial de saltos (en memoria de sesión)
        List<String> saltadas = (List<String>) session.getAttribute("saltadas");
        if (saltadas == null) saltadas = new ArrayList<>();

        if (excluirTemporal != null && !excluirTemporal.isEmpty()) {
            if (!saltadas.contains(excluirTemporal)) saltadas.add(excluirTemporal);
        }
        session.setAttribute("saltadas", saltadas);

        try {
            // 4. Llamada al servicio con la nueva lógica multianuncio/multifiltro
            MovieDTO recomendacion = recomendadorService.obtenerRecomendacion(genero, decada, rating, persona, saltadas);
            model.addAttribute("pelicula", recomendacion);
        } catch (Exception e) {
            model.addAttribute("errorMensaje", "No se encontró ninguna película con esos filtros.");
        }

        return "principal";
    }

    // --- AÑADIR AL DIARIO ---
    @PostMapping("/guardar")
    public String guardar(@RequestParam String titulo,
                          @RequestParam String poster,
                          @RequestParam double estrellas) {

        PeliculaValorada peli = new PeliculaValorada();
        peli.setTitulo(titulo);
        peli.setPoster(poster);
        peli.setEstrellas(estrellas);

        peliculaRepository.save(peli);
        return "redirect:/diario";
    }

    // --- DESCARTAR PARA SIEMPRE (No volverá a salir nunca) ---
    @PostMapping("/descartar")
    public String descartar(@RequestParam String titulo,
                            @RequestParam String genero,
                            HttpSession session,
                            Model model) {

        descarteRepository.save(new PeliculaDescartada(titulo));
        // Recargamos recomendación manteniendo el género actual
        return recomendar(genero, null, 0.0, null, null, session, model);
    }

    // --- VER EL DIARIO ---
    @GetMapping("/diario")
    public String verDiario(Model model) {
        model.addAttribute("peliculas", peliculaRepository.findAll());
        return "diario";
    }
}