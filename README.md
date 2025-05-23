# Futbol5

Aplicación para la gestión de canchas de fútbol 5 y organización de partidos y torneos. Incluye una API con persistencia en base de datos y un frontend básico para administradores de canchas y jugadores.

## Descripción

Esta aplicación permite:
- A administradores: gestionar canchas, horarios y reservas.
- A jugadores: buscar canchas, organizar partidos o torneos y unirse a los encuentros.

## Levantar la aplicación

1. Asegúrate de tener Docker y Docker Compose instalados.
2. En la raíz del proyecto, ejecuta:

```sh
docker compose up --build
```

- El flag `--build` es opcional, pero útil si hay cambios en el código o dependencias.

3. Para detener y eliminar los contenedores, ejecuta:

```sh
docker compose down
```

## URLs principales

- **Swagger (API docs):** [http://localhost:30002/swagger-ui/index.html#/](http://localhost:30002/swagger-ui/index.html#/)
- **Frontend:** [http://localhost:30003/](http://localhost:30003/)

## Tecnologías principales

- **Backend:** Java + Spring Boot
- **Frontend:** React + Vite
- **Base de datos:** PostgreSQL
- **Contenedores:** Docker y Docker Compose
