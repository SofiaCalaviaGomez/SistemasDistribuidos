# MovieRec: Sistema de Recomendación Multiusuario
**MovieRec** es una plataforma web integral para la gestión y recomendación de cine. El proyecto utiliza una arquitectura distribuida donde un backend en **Java (Spring Boot)** gestiona la lógica de negocio y usuarios, mientras que un microservicio en **Python (Flask)** actúa como motor de recomendaciones conectado a la API externa de películas de **TMDB**.

## Características Principales

* **Autenticación Segura:** Sistema de registro y login con cifrado de contraseñas.
* **Perfiles Privados:** Cada usuario dispone de su propio historial de películas valoradas y lista de descartes.
* **Recomendador Inteligente:** Motor de búsqueda que filtra por género, década, puntuación mínima y actores/directores.
* **Diario de Cine:** Galería estética donde el usuario guarda sus películas con valoración por estrellas.
* **Arquitectura Robusta:** Gestión avanzada de excepciones en comunicaciones HTTP y persistencia de datos.

---------------------
## Stack Tecnológico

### **Backend & Web**
* **Java 17+** con **Spring Boot 3**
* **Spring Security:** Gestión de sesiones y protección de rutas.
* **Spring Data JPA:** Abstracción de base de datos con Hibernate.
* **H2 Database:** Base de datos en memoria para agilidad en desarrollo.
* **Thymeleaf:** Motor de plantillas dinámicas.

### **Microservicio de Datos**
* **Python 3.x** con **Flask**
* **TMDB API:** Integración con la base de datos de cine más grande del mundo.
* **Requests:** Comunicación fluida entre servicios.

---------------------
## Arquitectura del Sistema

El sistema sigue un patrón de **Arquitectura en Capas**:
1.  **Capa de Presentación (Thymeleaf/CSS):** Interfaz de usuario dinámica.
2.  **Capa de Control (Spring MVC):** Gestión de peticiones y flujo de navegación.
3.  **Capa de Servicio (Lógica):** Procesamiento de datos y comunicación con Python.
4.  **Capa de Persistencia (JPA/Repository):** Mapeo de objetos a la base de datos relacional.

## Instalación y Configuración

### 1. Requisitos Previos
* Java JDK 17 o superior.
* Python 3.x instalado.
* Maven (o usar el wrapper de IntelliJ).

### 2. Configurar el Microservicio (Python)
Accede a la carpeta del recomendador e instala las dependencias:
```bash
cd peliculas
pip install flask requests
python app.py
