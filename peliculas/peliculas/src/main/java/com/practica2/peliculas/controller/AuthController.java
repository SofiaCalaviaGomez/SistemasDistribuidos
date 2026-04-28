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
    private UsuarioService usuarioService; // Inyecta la lógica de negocio para usuarios

    // Carga la página de bienvenida (index.html)
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Muestra el formulario de inicio de sesión
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Muestra el formulario de registro y prepara un objeto Usuario vacío
    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario()); // ThymeLeaf usará este objeto para guardar los datos
        return "registro";
    }

    // Recibe los datos del formulario de registro
    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario, Model model) {
        // Mensajes de control en la consola de IntelliJ
        System.out.println("LOG: Intentando registrar al usuario: " + usuario.getUsername());
        System.out.println("LOG: Nombre recibido: " + usuario.getNombre());

        try {
            // Llama al servicio para encriptar la clave y guardar en la base de datos
            usuarioService.registrar(usuario);
            // Si todo sale bien, redirige al login con un mensaje de éxito
            return "redirect:/login?exito=true";
        } catch (Exception e) {
            // GESTIÓN DE EXCEPCIONES: Si falla (ej. usuario duplicado), muestra error en el Front
            model.addAttribute("error", "El nombre de usuario ya existe o los datos son incorrectos.");
            model.addAttribute("usuario", usuario); // Devuelve los datos para no borrar lo que escribió el usuario
            return "registro";
        }
    }
}