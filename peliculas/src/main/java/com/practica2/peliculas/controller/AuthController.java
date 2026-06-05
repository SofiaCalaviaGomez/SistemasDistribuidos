package com.practica2.peliculas.controller;

import com.practica2.peliculas.model.Usuario;
import com.practica2.peliculas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JavaMailSender mailSender;

    // Carga la página de bienvenida (index.html)
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Muestra el formulario de inicio de sesión
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

    // Recibe los datos del formulario de registro y envía el email de confirmación
    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario, Model model) {
        try {
            // Genera un token secreto único para este usuario
            String token = UUID.randomUUID().toString();
            usuario.setTokenActivacion(token);

            // Y lo guarda por defecto como inactivo (se gestiona dentro del service)
            usuario.setActivo(false);
            usuarioService.registrar(usuario);

            // Se construye el enlace que el usuario pinchará en su correo
            String urlActivacion = "http://localhost:8080/activar?token=" + token;

            // Envia el correo a través del puerto SMTP de Mailpit (Docker)
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom("no-reply@movierec.com");
            mail.setTo(usuario.getEmail());
            mail.setSubject("Activa tu cuenta de Diario de Cine");
            mail.setText("¡Hola " + usuario.getNombre() + "!\n\n" +
                    "Para confirmar la creación de tu cuenta y activar tu acceso al recomendador de películas, haz clic en el siguiente enlace:\n" +
                    urlActivacion + "\n\n" +
                    "¡Nos vemos en el cine! 🎬🍿");

            mailSender.send(mail);

            // Lo redirijo al login avisando de que mire su bandeja de entrada
            return "redirect:/login?necesitaActivacion=true";

        } catch (Exception e) {
            // Imprimo la pila del error en rojo en la consola de IntelliJ para saber EXACTAMENTE qué falla
            e.printStackTrace();

            // Y se pasa el mensaje real del error a la interfaz para no dar falsos positivos de duplicados
            model.addAttribute("error", "Error en el registro: " + e.getMessage());
            model.addAttribute("usuario", usuario);
            return "registro";
        }
    }

    // Procesa el clic del usuario en el enlace de activación de su email
    @GetMapping("/activar")
    public String activarCuenta(@RequestParam("token") String token) {
        boolean activado = usuarioService.activarPorToken(token);

        if (activado) {
            return "redirect:/login?cuentaActivada=true";
        } else {
            return "redirect:/login?errorToken=true";
        }
    }
}