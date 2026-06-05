package com.practica2.peliculas.controller;

import com.practica2.peliculas.model.Usuario;
import com.practica2.peliculas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    // Carga la página de bienvenida (index.html)
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Muestra el formulario de inicio de sesión (¡ESTE ERA EL QUE FALTA!)
    @GetMapping("/login")
    public String login() {
        return "login"; // Busca templates/login.html sin redirigir
    }

    // Muestra el formulario de registro
    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // Recibe los datos del formulario de registro
    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario, Model model) {
        try {
            usuarioService.registrar(usuario);
            return "redirect:/login?exito=true";
        } catch (Exception e) {
            model.addAttribute("error", "El nombre de usuario ya existe o los datos son incorrectos.");
            model.addAttribute("usuario", usuario);
            return "registro";
        }
    }
}