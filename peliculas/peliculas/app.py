from flask import Flask, request, jsonify
import requests
import random

app = Flask(__name__)

# --- CONFIGURACIÓN TMDB ---
# Nota: Mantengo tu clave, pero recuerda protegerla en producción
TMDB_KEY = 'f5317450da7ca9517c85e039a3a71347'
TMDB_DISCOVER_URL = 'https://api.themoviedb.org/3/discover/movie'
TMDB_SEARCH_PERSON_URL = 'https://api.themoviedb.org/3/search/person'
TMDB_IMAGE_BASE = 'https://image.tmdb.org/t/p/w500'

@app.route('/api/recomendar', methods=['GET'])
def recomendar():
    # 1. Capturar parámetros enviados desde Java
    genero_web = request.args.get('genero', 'random').lower()
    decada = request.args.get('decada', '')
    rating_min = request.args.get('rating', 0)
    persona_nombre = request.args.get('persona', '').strip()
    texto_excluidas = request.args.get('excluir', "")

    # Procesar lista de exclusión (vistas y saltadas)
    lista_excluidas = [t.strip().lower() for t in texto_excluidas.split(",") if t.strip()]

    # Diccionario de IDs oficiales de TMDB
    ids_generos = {
        'action': 28, 'adventure': 12, 'animation': 16, 'comedy': 35,
        'drama': 18, 'horror': 27, 'romance': 10749, 'sci-fi': 878
    }

    # 2. Configurar parámetros base para la consulta a TMDB
    params = {
        'api_key': TMDB_KEY,
        'language': 'es-ES',
        'sort_by': 'popularity.desc',
        'include_adult': 'false',
        'page': 1,
        'vote_average.gte': rating_min,
        'vote_count.gte': 100  # Filtro para evitar pelis muy desconocidas o sin votos
    }

    # --- Lógica de Género (Random o específico) ---
    if genero_web == 'random' or genero_web not in ids_generos:
        # Si es random, elegimos un ID al azar del diccionario
        genero_id = random.choice(list(ids_generos.values()))
        params['with_genres'] = genero_id
        print(f"🎲 Modo Random: Género ID {genero_id} seleccionado.")
    else:
        params['with_genres'] = ids_generos[genero_web]

    # --- Filtro de Década ---
    if decada:
        params['primary_release_date.gte'] = f"{decada}-01-01"
        params['primary_release_date.lte'] = f"{int(decada)+9}-12-31"

    try:
        # --- Filtro de Persona(s) (Soporta múltiples nombres separados por coma) ---
        if persona_nombre:
            nombres = [n.strip() for n in persona_nombre.split(",") if n.strip()]
            ids_personas = []

            for nombre in nombres:
                res_p = requests.get(TMDB_SEARCH_PERSON_URL, params={
                    'api_key': TMDB_KEY,
                    'query': nombre
                })
                resultados = res_p.json().get('results', [])
                if resultados:
                    # Tomamos el ID del resultado más relevante
                    ids_personas.append(str(resultados[0]['id']))
                    print(f"🎬 Persona encontrada: {nombre} (ID: {resultados[0]['id']})")

            if ids_personas:
                # Unimos con comas para que TMDB busque pelis donde aparezcan estas personas
                params['with_people'] = ",".join(ids_personas)

        # 3. Petición de "Discover" a TMDB
        print(f"DEBUG: Llamando a TMDB con params -> {params}")
        res = requests.get(TMDB_DISCOVER_URL, params=params, timeout=7)
        res.raise_for_status() # Lanza error si la API responde mal

        data = res.json()
        peliculas = data.get('results', [])

        # 4. Lógica de exclusión (No recomendar lo que ya se vio o se saltó)
        # Usamos un generador para encontrar la primera peli que no esté en la lista negra
        peli_seleccionada = next((p for p in peliculas if p['title'].lower() not in lista_excluidas), None)

        if not peli_seleccionada:
            print("⚠️ No hay más resultados que no estén en la lista de excluidas.")
            return jsonify({"code": "ERR_NO_RESULTS"}), 404

        # 5. Construir y enviar respuesta JSON
        return jsonify({
            "title": peli_seleccionada['title'],
            "poster": f"{TMDB_IMAGE_BASE}{peli_seleccionada.get('poster_path')}" if peli_seleccionada.get('poster_path') else "https://via.placeholder.com/500x750?text=Sin+Poster",
            "plot": peli_seleccionada.get('overview') or "Sin descripción disponible.",
            "year": peli_seleccionada.get('release_date', 'N/A')[:4]
        })

    except requests.exceptions.RequestException as e:
        print(f"❌ Error de conexión con TMDB: {e}")
        return jsonify({"code": "ERR_CONEXION"}), 502
    except Exception as e:
        print(f"❌ Error interno: {e}")
        return jsonify({"code": "ERR_GENERICO"}), 500

if __name__ == '__main__':
    # Ejecutamos en el puerto 5000 para conectar con Spring Boot
    print("Servidor de Recomendación Python iniciado en puerto 5000")
    app.run(port=5000, debug=True)