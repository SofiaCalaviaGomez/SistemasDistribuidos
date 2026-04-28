package com.practica2.peliculas.exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Locale;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(PythonApiException.class)
    public String handlePythonError(PythonApiException ex, Model model, Locale locale) {

        // 1. Buscamos la traducción
        String mensajeTraducido = messageSource.getMessage(
                "error." + ex.getErrorCode(),
                null,
                "Ha ocurrido un error inesperado en el motor de recomendaciones.",
                locale
        );

        // 2. Añadimos el mensaje al modelo para Thymeleaf
        model.addAttribute("errorMensaje", mensajeTraducido);

        // 3. Devolvemos la vista
        return "principal";
    }
}