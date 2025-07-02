<p align="center">
  <img src=docs/img/logo_3.webp width="350" alt="Fulbito Logo">
</p>

---

# Fulbito

**Platform for managing 5-a-side soccer fields and organizing recreational matches and tournaments.**
This system is designed to improve the experience for both players and venue owners, solving common challenges in coordinating sporting events.

---

## Index

1. [🚀 Overview](#overview)
2. [🧑‍💻 Main Features](#main-features)
3. [🧱 Project Architecture](#project-architecture)
4. [⚙️ Technologies Used](#technologies-used)
5. [🐳 Developer Instructions](#developer-instructions)
   * [Running the Complete Environment with Docker](#running-the-complete-environment-with-docker)
   * [Debug Mode (Local Development)](#debug-mode-local-development)
6. [🌐 Main URLs](#-main-urls)

---

## Overview

**Fulbito** is a comprehensive platform that supports different user profiles:

* **Players** who want to find fields, coordinate matches, join teams, or participate in tournaments.
* **Organizers** looking to create larger or recurring sporting events.
* **Venue owners** who need to manage field availability, reservations, and schedules.

The system is designed to deliver a smooth experience, reduce friction in organizing matches, and encourage participation in recreational sports activities.

---

## Main Features

### For Players

* Search available fields by area, date, and time.
* Join open matches or create new ones.
* Form teams and participate in tournaments.
* Confirm or cancel attendance with one click.

### For Venue Owners

* Publish available fields, schedules, and pricing.
* Receive and manage reservations.
* Avoid overlapping bookings.
* View usage statistics.

### For Organizers

* Create and manage tournaments.
* Invite players and teams.
* Configure rules and tournament formats.
* Display match results and statistics.

---

## Project Architecture

This project is divided into three main components:

* **Backend:** REST API built with Spring Boot.
* **Frontend:** Web application using React and Vite.
* **Database:** PostgreSQL for data persistence.

The entire development and runtime environment can be launched using Docker.

---

## Technologies Used

| Component     | Technology               |
|---------------|--------------------------|
| Languages     | Java, TypeScript         |
| Backend       | Spring Boot              |
| Frontend      | React + Tailwind + Vite  |
| Database      | PostgreSQL               |
| Documentation | Swagger                  |
| Containers    | Docker, Docker Compose   |

---

## Developer Instructions

### Running the Complete Environment with Docker

1. Make sure you have [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) installed.
2. In the project root, run:

```bash
docker compose up --build
```

3. To stop and remove the containers:

```bash
docker compose down
```

This will start:

* API at `localhost:30002`
* Frontend at `localhost:30003`
* Internal PostgreSQL database

---

### Debug Mode (Local Development)

1. Configure the required environment variables in your IDE (e.g., IntelliJ IDEA):

* `SPRING_DATASOURCE_DRIVER_CLASS_NAME`
* `SPRING_DATASOURCE_URL`
* `SPRING_DATASOURCE_USERNAME`
* `SPRING_DATASOURCE_PASSWORD`

2. Start only the database with:

```bash
docker compose up db
```

3. Run the backend from your IDE in debug mode.

4. Access the API documentation at:

```
http://localhost:30002/swagger-ui/index.html#
```

---

## Main URLs

| Resource           | URL                                                                                              |
| ------------------ | ------------------------------------------------------------------------------------------------ |
| Swagger (API Docs) | [http://localhost:30002/swagger-ui/index.html#/](http://localhost:30002/swagger-ui/index.html#/) |
| Frontend Web App   | [http://localhost:30003/](http://localhost:30003/)                                               |

---
