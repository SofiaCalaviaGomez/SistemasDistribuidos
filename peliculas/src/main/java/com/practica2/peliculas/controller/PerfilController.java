package com.practica2.peliculas.controller;

import com.practica2.peliculas.model.Usuario;
import com.practica2.peliculas.repository.UsuarioRepository;
import com.practica2.peliculas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    public String verPerfil(Model model, Authentication auth) {
        Usuario usuarioActual = usuarioRepository.findByUsername(auth.getName()).get();
        model.addAttribute("usuario", usuarioActual);
        return "perfil";
    }

    // --- ACTUALIZAR DATOS (UPDATE) ---
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@RequestParam String nombre,
                                   @RequestParam(required = false) String password,
                                   Authentication auth,
                                   Model model) {
        try {
            Usuario usuarioActual = usuarioRepository.findByUsername(auth.getName()).get();
            usuarioService.actualizar(usuarioActual, nombre, password);
            return "redirect:/perfil?exito=true";
        } catch (Exception e) {
            model.addAttribute("error", "No se pudieron actualizar los datos del perfil.");
            Usuario usuarioActual = usuarioRepository.findByUsername(auth.getName()).get();
            model.addAttribute("usuario", usuarioActual);
            return "perfil";
        }
    }

    // --- ELIMINAR CUENTA (DELETE) ---
    @PostMapping("/perfil/eliminar")
    public String eliminarCuenta(Authentication auth, HttpSession session) {
        Usuario usuarioActual = usuarioRepository.findByUsername(auth.getName()).get();
        usuarioService.eliminar(usuarioActual);
        session.invalidate(); // Destruyo la sesión del navegador
        return "redirect:/login?eliminado=true";
    }
}