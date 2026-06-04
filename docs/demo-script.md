# Demo Script

Target duration: 3 to 5 minutes.

## 1. Introduce The Problem

Explain that universities need to schedule lectures, labs, seminars, and exams while avoiding professor, room, and student group clashes.

State the CSP model:

```text
P = (X, D, C)
```

## 2. Show The Dashboard

Open the application and sign in as `admin`.

Show:

- Course count
- Professor count
- Room count
- Course event count
- Latest solver job
- Algorithm pipeline summary

## 3. Load Demo Data

Open `Run FAF demo`.

Click `Load demo data`.

Explain that the dataset includes faculties, departments, rooms, professors, student groups, courses, course events, timeslots, preferences, and exams.

## 4. Show Academic Inputs

Open generated CRUD pages for:

- Courses
- Professors
- Rooms
- Student groups
- Timeslots
- Course events

Point out that this is a real application, not just standalone algorithm code.

## 5. Generate Timetable

Return to `Run FAF demo` and click `Generate timetable`.

Explain that the request creates an asynchronous solver job and does not block the HTTP request.

## 6. Show Solver Progress

Point to each visible status:

- Validating input
- Building conflict graph
- Running Welsh-Powell
- Running AC-3
- Running backtracking
- Scoring soft constraints
- Completed

## 7. Show Final Timetable

Open `View weekly timetable`.

Show:

- Days as columns
- Timeslots as rows
- Event cards with course, professor, group, room, and building
- Hard conflicts equal to zero
- Soft penalty score

## 8. Show Solver Statistics

Show:

- Backtrack count
- Domain reductions
- Runtime
- Soft penalty
- Hard conflict count

## 9. Explain Algorithm Usage

Open `Explain algorithms`.

Summarize:

- Welsh-Powell creates an initial timeslot coloring from the conflict graph.
- AC-3 removes impossible values from domains.
- MAC backtracking searches assignments with MRV and LCV.
- Soft scoring ranks the produced valid schedule.

## 10. Show Exam Schedule

Open `View exam schedule`.

Explain that exams are also represented as graph vertices, conflicts are shared student groups, and colors become exam periods.

## 11. Mention Limitations And Future Work

Mention:

- Local search could improve soft score after MAC.
- PDF export can be added.
- Kafka could stream progress events.
- Kubernetes could deploy the system.
- Timefold can be used later as a benchmark, not as the implemented solver.
