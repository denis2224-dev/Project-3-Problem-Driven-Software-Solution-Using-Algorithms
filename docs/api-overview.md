# API Overview

Authentication uses JHipster JWT. Admin-only endpoints require `ROLE_ADMIN`.

## Demo Data

```http
POST /api/demo-data/load
DELETE /api/demo-data/clear
```

## Solver Jobs

```http
POST /api/solver-jobs/generate-timetable
GET /api/solver-jobs
GET /api/solver-jobs/{id}
GET /api/solver-jobs/{id}/result
POST /api/solver-jobs/{id}/cancel
GET /api/solver-jobs/{id}/statistics
```

## Timetables

```http
GET /api/timetables
GET /api/timetables/{id}
POST /api/timetables/{id}/approve
POST /api/timetables/{id}/publish
```

## Timetable Versions

```http
GET /api/timetable-versions
GET /api/timetable-versions/{id}
GET /api/timetable-versions/{id}/entries
GET /api/timetable-versions/{id}/export/csv
```

## Academic CRUD

Generated JHipster CRUD endpoints:

```http
/api/faculties
/api/departments
/api/buildings
/api/rooms
/api/professors
/api/student-groups
/api/courses
/api/course-events
/api/timeslots
/api/professor-preferences
```

## Exam Scheduling

```http
GET /api/exams
GET /api/exams/coloring
GET /api/exam-schedule-entries
```

`GET /api/exams/coloring` returns a Welsh-Powell graph-coloring result for exams. Each color is a suggested exam period.

## Reports

```http
GET /api/timetable-versions/{id}/export/csv
```

The CSV export includes day, start time, end time, course, event type, professor, student group, room, and building.
