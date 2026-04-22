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

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        // Esto crea el objeto vacío que Thymeleaf rellenará
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario, Model model) {
        System.out.println("LOG: Intentando registrar al usuario: " + usuario.getUsername());
        System.out.println("LOG: Nombre recibido: " + usuario.getNombre());
        try {
            // Intentamos registrar
            usuarioService.registrar(usuario);
            return "redirect:/login?exito=true";
        } catch (Exception e) {
            // Si el username ya existe o hay error de BD, volvemos al formulario con error
            model.addAttribute("error", "El nombre de usuario ya existe o los datos son incorrectos.");
            model.addAttribute("usuario", usuario); // Mantenemos los datos para que no tenga que escribir todo otra vez
            return "registro";
        }
    }
}