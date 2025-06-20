<p align="center">
  <img src=docs/img/logo_3.webp width="350" alt="Logo Futbol5">
</p>

---

# Futbol5

**Plataforma para la gestión de canchas de fútbol 5 y organización de partidos y torneos recreativos.**
Este sistema está pensado para facilitar la experiencia tanto de los jugadores como de los dueños de canchas, resolviendo los desafíos comunes en la coordinación de encuentros deportivos.

---

## Índice

1. [🚀 Descripción General](#descripción-general)
2. [🧑‍💻 Funcionalidades Principales](#funcionalidades-principales)
3. [🧱 Arquitectura del Proyecto](#arquitectura-del-proyecto)
4. [⚙️ Tecnologías Utilizadas](#tecnologías-utilizadas)
5. [🐳 Instrucciones para Desarrolladores](#instrucciones-para-desarrolladores)
    * [Levantando el entorno completo con Docker](#levantando-el-entorno-completo-con-docker)
    * [Modo Debug (desarrollo local)](#modo-debug-desarrollo-local)
6. [🌐 URLs Principales](#-urls-principales)

---

## Descripción General

**Futbol5** es una plataforma integral que permite a distintos perfiles de usuarios:

* **Jugadores** que desean encontrar canchas, coordinar partidos, sumarse a equipos o participar en torneos.
* **Organizadores** que buscan armar eventos deportivos más grandes o regulares.
* **Dueños de canchas** que necesitan gestionar la disponibilidad de sus instalaciones, reservas y horarios.

El sistema está diseñado para brindar una experiencia fluida, reducir la fricción en la organización de partidos y fomentar la participación en actividades deportivas recreativas.

---

## Funcionalidades Principales

### Para jugadores

* Buscar canchas disponibles por zona, fecha y horario.
* Unirse a partidos abiertos o crear partidos nuevos.
* Formar equipos y participar en torneos.
* Confirmar o cancelar asistencia con un clic.

### Para dueños de canchas

* Publicar canchas disponibles, sus horarios y precios.
* Recibir y gestionar reservas.
* Evitar solapamientos de horarios.
* Ver estadísticas de uso.

### Para organizadores
* Crear y gestionar torneos.
* Invitar jugadores y equipos.
* Configurar reglas y formatos de torneo.
* Visualizar resultados y estadísticas de los partidos.

---

## Arquitectura del Proyecto

Este proyecto está dividido en tres grandes componentes:

* **Backend:** API REST construida con Spring Boot.
* **Frontend:** Aplicación web con React y Vite.
* **Base de datos:** PostgreSQL para persistencia de datos.

Todo el entorno de desarrollo y ejecución puede levantarse usando Docker.

---

## Tecnologías Utilizadas

| Componente    | Tecnología              |
|---------------|-------------------------|
| Lenguajes     | Java, TypeScript        |
| Backend       | Spring Boot             |
| Frontend      | React + Tailwind + Vite |
| Base de Datos | PostgreSQL              |
| Documentación | Swagger                 |
| Contenedores  | Docker, Docker Compose  |

---

## Instrucciones para Desarrolladores

### Levantando el entorno completo con Docker

1. Asegúrate de tener [Docker](https://www.docker.com/) y [Docker Compose](https://docs.docker.com/compose/) instalados.
2. En la raíz del proyecto, ejecutá:

```bash
docker compose up --build
```

3. Para detener y eliminar los contenedores:

```bash
docker compose down
```

Esto levantará:

* API en `localhost:30002`
* Frontend en `localhost:30003`
* Base de datos PostgreSQL interna

---

### Modo Debug (desarrollo local)

1. Configurar las variables de entorno necesarias en tu IDE (por ejemplo, IntelliJ IDEA):

* `SPRING_DATASOURCE_DRIVER_CLASS_NAME`
* `SPRING_DATASOURCE_URL`
* `SPRING_DATASOURCE_USERNAME`
* `SPRING_DATASOURCE_PASSWORD`

2. Levantar únicamente la base de datos con:

```bash
docker compose up db
```

3. Ejecutar el backend desde el IDE en modo debug.

4. Acceder a la documentación de la API en:

```
http://localhost:30002/swagger-ui/index.html#
```

---

## URLs Principales

| Recurso            | URL                                                                                              |
| ------------------ | ------------------------------------------------------------------------------------------------ |
| Swagger (API Docs) | [http://localhost:30002/swagger-ui/index.html#/](http://localhost:30002/swagger-ui/index.html#/) |
| Frontend Web App   | [http://localhost:30003/](http://localhost:30003/)                                               |

---
