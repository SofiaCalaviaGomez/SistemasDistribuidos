package com.practica2.peliculas.controller;

import com.practica2.peliculas.model.Usuario;
import com.practica2.peliculas.repository.UsuarioRepository;
import com.practica2.peliculas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    // --- VER PERFIL (READ) ---
    @GetMapping("/perfil")
    public String verPerfil(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Buscamos de forma segura usando las credenciales del UserDetails inyectado
        Usuario usuarioActual = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la sesión"));

        model.addAttribute("usuario", usuarioActual);
        return "perfil";
    }

    // --- ACTUALIZAR DATOS (UPDATE) ---
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@RequestParam String nombre,
                                   @RequestParam(required = false) String password,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        try {
            Usuario usuarioActual = usuarioRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuarioService.actualizar(usuarioActual, nombre, password);
            return "redirect:/perfil?exito";

        } catch (Exception e) {
            e.printStackTrace(); // Pintamos la traza en la consola de IntelliJ por si acaso

            model.addAttribute("error", "No se pudieron actualizar los datos del perfil: " + e.getMessage());

            // Recarga de emergencia segura para evitar que la plantilla pete al buscar el objeto 'usuario'
            Usuario usuarioActual = usuarioRepository.findByUsername(userDetails.getUsername()).orElse(new Usuario());
            model.addAttribute("usuario", usuarioActual);
            return "perfil";
        }
    }

    // --- ELIMINAR CUENTA (DELETE) ---
    @PostMapping("/perfil/eliminar")
    public String eliminarCuenta(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        Usuario usuarioActual = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioService.eliminar(usuarioActual);
        session.invalidate(); // Destruyo la sesión del navegador
        return "redirect:/login?eliminado=true";
    }
}