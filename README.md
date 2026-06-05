# Automated University Timetabling and Exam Scheduling System

University project for the course **Algorithms Analysis and Implementation**.

This repository contains a working full-stack application that demonstrates how classic algorithms solve a real university scheduling problem. The system models timetable generation as a Constraint Satisfaction Problem:

```text
P = (X, D, C)
```

- `X`: course events and exams to schedule.
- `D`: possible assignments, such as `(timeslot, room)`.
- `C`: hard and soft constraints.

Hard constraints include professor clashes, student group clashes, room double-booking, room capacity, and equipment compatibility. Soft constraints include idle gaps, professor preferences, building transitions, and schedule balance.

## Tech Stack

- Backend: Java 21, Spring Boot, JHipster monolith
- Frontend: Angular
- Database: PostgreSQL
- Authentication: JHipster JWT with default admin/user roles
- Build: Maven and npm
- Database migrations: Liquibase
- Local infrastructure: Docker Compose
- Architecture: Modular monolith with asynchronous solver jobs

## Architecture

```text
Angular Frontend
    |
Spring Boot REST API
    |
PostgreSQL Database
    |
Solver Job Orchestration
    |
Java Solver Core
```

The solver is not a blocking controller method. The frontend creates a solver job, the backend persists it, an asynchronous service updates job stages, and the generated result is stored as a timetable version.

Solver statuses:

- `CREATED`
- `VALIDATING_INPUT`
- `BUILDING_CONFLICT_GRAPH`
- `RUNNING_WELCH_POWELL`
- `RUNNING_AC3`
- `RUNNING_BACKTRACKING`
- `SCORING_SOFT_CONSTRAINTS`
- `COMPLETED`
- `FAILED`
- `CANCELLED`

## Algorithms

Implemented manually in Java under `src/main/java/com/unischeduler/solver`.

- Welsh-Powell graph coloring: builds a conflict graph and creates an initial timeslot coloring.
- Backtracking search with AC-3 / MAC: assigns valid timeslot-room values while maintaining arc consistency.
- MRV: selects the variable with the smallest remaining domain.
- LCV: tries values that constrain neighboring variables least.
- Soft constraint scoring: scores idle gaps, preference violations, transitions, and edge-hour pressure.

Enterprise solvers such as Timefold/OptaPlanner are intentionally not used as the main solver. They are documented only as possible future benchmark comparisons.

## Domain Model Summary

Core academic entities:

- Faculty, Department, Building, Room
- Professor, StudentGroup, Course, CourseEvent
- Timeslot, ProfessorPreference

Scheduling entities:

- SolverJob
- Timetable, TimetableVersion, TimetableEntry
- ScheduleConflict

Exam entities:

- Exam
- ExamScheduleEntry

## Run With Docker

The complete demo environment is dockerized. A professor or teammate can clone the repository and start the application without installing Java, Maven, Node.js, npm, or PostgreSQL locally.

Start everything:

```bash
docker compose up --build
```

Expected containers:

- `postgres`: PostgreSQL database with a persistent Docker volume.
- `backend`: Spring Boot API, Liquibase migrations, authentication, solver jobs, and Java solver core.
- `frontend`: Angular production build served by nginx and reverse-proxied to the backend.

Open the application:

```text
http://localhost:4200
```

Useful direct endpoints:

```text
Backend API: http://localhost:8080
Health:      http://localhost:8080/management/health
```

Stop the stack:

```bash
docker compose down
```

Stop the stack and remove the PostgreSQL volume:

```bash
docker compose down -v
```

If PostgreSQL exits after a previous failed Docker startup, run `docker compose down -v` once, then retry `docker compose up --build`.

Docker environment variables can be overridden through a local `.env` file. Start from the committed template:

```bash
cp .env.example .env
```

Real `.env` files are ignored by Git. The defaults in `docker-compose.yml` are local-demo values only and must be replaced for any shared deployment.

Security note: the default Docker stack uses PostgreSQL trust authentication and JHipster's `secret-samples` profile only to make a local university demo start with one command. Do not use those defaults for production or shared infrastructure.

## How To Run Locally Without Full Docker

Prerequisites:

- Java 21
- Node.js compatible with the generated JHipster version
- Docker Desktop or Docker Engine
- PostgreSQL through the provided Docker Compose file

Start PostgreSQL:

```bash
docker compose -f src/main/docker/postgresql.yml up --wait
```

Run the backend:

```bash
./mvnw -Dskip.installnodenpm -Dskip.npm -ntp --batch-mode
```

Run the frontend in another terminal:

```bash
npm start
```

Open:

```text
http://localhost:4200
```

Default JHipster accounts:

- Admin: `admin` / `admin`
- User: `user` / `user`

## How To Load Demo Data

Use the UI:

1. Sign in as `admin`.
2. Open `Run FAF demo` from the dashboard.
3. Click `Load demo data`.

Or call the endpoint:

```bash
curl -X POST http://localhost:8080/api/demo-data/load \
  -H "Authorization: Bearer <JWT>"
```

The demo dataset includes 1 faculty, 2 departments, 3 buildings, 10 rooms, 15 professors, 8 student groups, 20 courses, 40 course events, 30 timeslots, professor preferences, and exams.

## How To Generate A Timetable

Use the UI:

1. Open `Run FAF demo`.
2. Click `Generate timetable`.
3. Watch the status pipeline update.
4. Open `View weekly timetable` after completion.

Main endpoint:

```http
POST /api/solver-jobs/generate-timetable
```

Result endpoint:

```http
GET /api/solver-jobs/{id}/result
```

## API Overview

Academic CRUD endpoints are generated by JHipster under `/api` for faculties, departments, buildings, rooms, professors, student groups, courses, course events, timeslots, and preferences.

Custom endpoints:

- `POST /api/demo-data/load`
- `DELETE /api/demo-data/clear`
- `POST /api/solver-jobs/generate-timetable`
- `GET /api/solver-jobs/{id}/result`
- `POST /api/solver-jobs/{id}/cancel`
- `GET /api/solver-jobs/{id}/statistics`
- `GET /api/timetable-versions/{id}/entries`
- `GET /api/timetable-versions/{id}/export/csv`
- `POST /api/timetables/{id}/approve`
- `POST /api/timetables/{id}/publish`
- `GET /api/exams/coloring`

## Screenshots

Screenshots should be added before final presentation:

- Dashboard
- Demo scenario status tracker
- Weekly timetable grid
- Exam coloring schedule
- Algorithm explanation page

## Documentation

- [Architecture](docs/architecture.md)
- [Algorithms](docs/algorithms.md)
- [Demo Script](docs/demo-script.md)
- [API Overview](docs/api-overview.md)

## Future Improvements

- Kafka events for solver job progress
- Kubernetes deployment
- Timefold benchmark comparison
- Advanced local search or Tabu Search post-optimization
- PDF export
- Student-facing mobile application
