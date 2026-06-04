# Architecture

The application is a JHipster modular monolith. It keeps deployment simple for a university demo while separating academic data, solver orchestration, algorithm code, timetable persistence, reports, and UI concerns.

## System Architecture

```mermaid
flowchart LR
  UI[Angular Frontend] --> API[Spring Boot REST API]
  API --> DB[(PostgreSQL)]
  API --> Jobs[Solver Job Orchestration]
  Jobs --> Solver[Java Solver Core]
  Solver --> DB
  API --> Reports[CSV Reporting]
```

## Solver Workflow

```mermaid
stateDiagram-v2
  [*] --> CREATED
  CREATED --> VALIDATING_INPUT
  VALIDATING_INPUT --> BUILDING_CONFLICT_GRAPH
  BUILDING_CONFLICT_GRAPH --> RUNNING_WELCH_POWELL
  RUNNING_WELCH_POWELL --> RUNNING_AC3
  RUNNING_AC3 --> RUNNING_BACKTRACKING
  RUNNING_BACKTRACKING --> SCORING_SOFT_CONSTRAINTS
  SCORING_SOFT_CONSTRAINTS --> COMPLETED
  VALIDATING_INPUT --> FAILED
  BUILDING_CONFLICT_GRAPH --> FAILED
  RUNNING_BACKTRACKING --> FAILED
  CREATED --> CANCELLED
  RUNNING_BACKTRACKING --> CANCELLED
  COMPLETED --> [*]
  FAILED --> [*]
  CANCELLED --> [*]
```

## Data Flow

```mermaid
sequenceDiagram
  participant User
  participant Angular
  participant API
  participant JobService
  participant Solver
  participant DB

  User->>Angular: Click Generate Timetable
  Angular->>API: POST /api/solver-jobs/generate-timetable
  API->>DB: Insert SolverJob CREATED
  API->>JobService: Start async job
  JobService->>DB: Update status stages
  JobService->>Solver: Build graph, color, propagate, backtrack, score
  Solver-->>JobService: SolverResult
  JobService->>DB: Persist TimetableVersion and TimetableEntry rows
  Angular->>API: Poll /api/solver-jobs/{id}
  Angular->>API: GET /api/solver-jobs/{id}/result
  API->>DB: Read stored result
  API-->>Angular: Timetable version and entries
```

## Domain Relationship Overview

```mermaid
erDiagram
  Faculty ||--o{ Department : contains
  Department ||--o{ Course : offers
  Department ||--o{ Professor : employs
  Department ||--o{ StudentGroup : owns
  Building ||--o{ Room : contains
  Course ||--o{ CourseEvent : has
  Professor ||--o{ CourseEvent : teaches
  StudentGroup ||--o{ CourseEvent : attends
  Professor ||--o{ ProfessorPreference : has
  Timeslot ||--o{ ProfessorPreference : applies_to
  SolverJob ||--o{ TimetableVersion : produces
  Timetable ||--o{ TimetableVersion : versions
  TimetableVersion ||--o{ TimetableEntry : contains
  CourseEvent ||--o{ TimetableEntry : scheduled_as
  Room ||--o{ TimetableEntry : hosts
  Timeslot ||--o{ TimetableEntry : occurs_at
  Course ||--o{ Exam : has
  StudentGroup ||--o{ Exam : takes
  TimetableVersion ||--o{ ExamScheduleEntry : contains
```

## Algorithm Pipeline

```mermaid
flowchart LR
  A[Raw Input] --> B[Conflict Graph]
  B --> C[Welsh-Powell]
  C --> D[CSP Domains]
  D --> E[AC-3]
  E --> F[Backtracking MAC]
  F --> G[Soft Scoring]
  G --> H[Final Timetable]
```

## Package Structure

Important packages:

- `com.unischeduler.solver.graph`
- `com.unischeduler.solver.csp`
- `com.unischeduler.solver.ac3`
- `com.unischeduler.solver.backtracking`
- `com.unischeduler.solver.heuristics`
- `com.unischeduler.solver.scoring`
- `com.unischeduler.service`
- `com.unischeduler.web.rest`
