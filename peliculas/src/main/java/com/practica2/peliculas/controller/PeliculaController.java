package com.practica2.peliculas.controller;

import com.practica2.peliculas.model.MovieDTO;
import com.practica2.peliculas.model.PeliculaDescartada;
import com.practica2.peliculas.model.PeliculaValorada;
import com.practica2.peliculas.model.Usuario;
import com.practica2.peliculas.repository.DescarteRepository;
import com.practica2.peliculas.repository.PeliculaRepository;
import com.practica2.peliculas.repository.UsuarioRepository;
import com.practica2.peliculas.service.RecomendadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
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

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- PANTALLA PRINCIPAL ---
    // Filtro para que cada usuario solo vea sus propias películas recientes
    @GetMapping("/principal")
    public String principal(Model model, Authentication auth) {
        Usuario usuarioActual = usuarioRepository.findByUsername(auth.getName()).get();
        model.addAttribute("recientes", peliculaRepository.findTop4ByUsuarioOrderByIdDesc(usuarioActual));
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
                             Authentication auth,
                             Model model) {

        // Cargo los datos del usuario actual para mantener la interfaz actualizada
        Usuario usuarioActual = usuarioRepository.findByUsername(auth.getName()).get();
        model.addAttribute("recientes", peliculaRepository.findTop4ByUsuarioOrderByIdDesc(usuarioActual));

        // Y devuelve filtros a la vista
        model.addAttribute("generoSeleccionado", genero);
        model.addAttribute("decadaSeleccionada", decada);
        model.addAttribute("ratingSeleccionado", rating);
        model.addAttribute("personaSeleccionada", persona);

        // Memoria temporal de la sesión (películas saltadas)
        List<String> saltadas = (List<String>) session.getAttribute("saltadas");
        if (saltadas == null) saltadas = new ArrayList<>();

        if (excluirTemporal != null && !excluirTemporal.isEmpty()) {
            if (!saltadas.contains(excluirTemporal)) saltadas.add(excluirTemporal);
        }
        session.setAttribute("saltadas", saltadas);

        try {
            // Paso los filtros al microservicio de Python
            MovieDTO recomendacion = recomendadorService.obtenerRecomendacion(genero, decada, rating, persona, saltadas);
            model.addAttribute("pelicula", recomendacion);
        } catch (Exception e) {
            model.addAttribute("errorMensaje", "No se encontró ninguna película con esos filtros.");
        }

        return "principal";
    }

    // --- AÑADIR AL DIARIO (PRIVADO) ---
    @PostMapping("/guardar")
    public String guardar(@RequestParam String titulo,
                          @RequestParam String poster,
                          @RequestParam double estrellas,
                          Authentication auth) {

        // Busqued de quién es el usuario logueado
        Usuario usuarioActual = usuarioRepository.findByUsername(auth.getName()).get();

        PeliculaValorada peli = new PeliculaValorada();
        peli.setTitulo(titulo);
        peli.setPoster(poster);
        peli.setEstrellas(estrellas);
        peli.setUsuario(usuarioActual); // Le asigno el dueño a la película
        // La fecha se asigna sola gracias al @PrePersist de la Entidad

        peliculaRepository.save(peli);
        return "redirect:/diario";
    }

    // --- ACTUALIZAR REGISTRO DEL DIARIO (UPDATE) ---
    // Modifico las estrellas y la fecha de visionado de una película guardada
    @PostMapping("/editar/{id}")
    public String editarPelicula(@PathVariable Long id,
                                 @RequestParam double estrellas,
                                 @RequestParam String fecha,
                                 Authentication auth) {
        try {
            // Busca la película en el diario
            PeliculaValorada peli = peliculaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));

            // Y se Verifica que pertenezca al usuario que tiene la sesión activa
            if (!peli.getUsuario().getUsername().equals(auth.getName())) {
                return "redirect:/diario?error=unauthorized";
            }

            // Parseo la fecha introducida en el formulario (YYYY-MM-DD)
            LocalDate fechaIntroducida = LocalDate.parse(fecha);

            // Y valido si la fecha es estrictamente posterior a hoy, si no se detiene el proceso
            if (fechaIntroducida.isAfter(LocalDate.now())) {
                return "redirect:/diario?errorFecha=true";
            }

            // Si pasa la validación, modifica los valores
            peli.setEstrellas(estrellas);
            peli.setFechaGuardado(fechaIntroducida);

            // Guardo los cambios en la base de datos
            peliculaRepository.save(peli);
            return "redirect:/diario?editado=true";
        } catch (Exception e) {
            return "redirect:/diario?error=true";
        }
    }

    // --- DESCARTAR PARA SIEMPRE ---
    @PostMapping("/descartar")
    public String descartar(@RequestParam String titulo,
                            @RequestParam String genero,
                            HttpSession session,
                            Authentication auth,
                            Model model) {

        descarteRepository.save(new PeliculaDescartada(titulo));
        // Recargamos recomendación pasando el objeto de autenticación
        return recomendar(genero, null, 0.0, null, null, session, auth, model);
    }

    // --- VER EL DIARIO PERSONAL ---
    @GetMapping("/diario")
    public String verDiario(Model model, Authentication auth) {
        Usuario usuarioActual = usuarioRepository.findByUsername(auth.getName()).get();
        // Solo recuperamos las películas de este usuario
        model.addAttribute("peliculas", peliculaRepository.findByUsuario(usuarioActual));
        return "diario";
    }

    // --- ELIMINAR REGISTRO ---
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        peliculaRepository.deleteById(id);
        return "redirect:/diario";
    }
}