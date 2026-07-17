# DegreeTree

A course planning tool that helps college students visualize degree progress, figure out which
courses they are eligible to take next, and build out a multi-semester schedule.

Prerequisites are modeled as a logic tree (AND / OR operators over individual courses) stored in a
PostgreSQL database, so the app can correctly evaluate complex requirements like "CSC 1051 AND
(MAT 1500 OR MAT 2600)".

## Features

- **Eligible Courses** - Select the courses you have completed and the app computes which courses
  you are now eligible to take, respecting AND/OR prerequisite logic (evaluated on the backend).
- **Schedule Planner** - Drag and drop courses into semesters to build a term-by-term plan, with
  undo/redo, progress tracking, and local persistence.
- **Prerequisite trees** - Backend endpoints expose the full nested prerequisite structure for any
  course so it can be visualized as a graph.
- Light/dark theme toggle.

## Tech Stack

**Backend** (`DegreeTree-Backend/courseplanner`)
- Java 21, Spring Boot 3.5
- Spring Data JPA + PostgreSQL
- springdoc OpenAPI (Swagger UI)
- Maven

**Frontend** (`DegreeTree-Frontend`)
- React 18 + Vite
- Tailwind CSS
- Cytoscape (graph visualization)
- dnd-kit (drag and drop)
- Zustand

## Project Structure

```
DegreeTree/
  DegreeTree-Backend/courseplanner/   Spring Boot API
    src/main/java/.../Controller/     REST controllers
    src/main/java/.../Service/        Business logic (prerequisite tree building)
    src/main/java/.../Repository/     JPA repositories
    src/main/java/.../Entity/         Course and PrereqNode entities
    src/main/java/.../Service/        Business logic + CatalogSeeder
    src/main/java/.../dto/            Request/response DTOs
  DegreeTree-Frontend/                React app
    src/pages/                        Eligible Courses, Schedule Planner, About
    src/components/                   UI components (planner, graph, selectors)
    src/contexts/                     Planner and Theme state
    src/services/api.js               API client + eligibility logic
```

## Data Model

Prerequisites are stored as a tree using two tables:

- `course` - a course row (code, name, credits, description).
- `prereq_node` - one row per node in a course's prerequisite tree, stored as a self-referencing
  adjacency list (`parent_id` points at the parent node, null for the root).

Each `prereq_node` has a `node_type`:

- `AND` / `OR` - an operator node; its children must (all / at least one) be satisfied.
- `LEAF` - references a single prerequisite course via `leaf_course_id`.

A course with no prerequisites simply has no `prereq_node` rows. The backend walks this tree and
returns a nested JSON structure, and evaluates it against the user's completed courses to compute
eligibility.

## Getting Started

### Prerequisites

- Java 21
- Node.js 18+
- A running PostgreSQL instance

### Backend

The backend connects to PostgreSQL. Create a database and set your credentials in
`DegreeTree-Backend/courseplanner/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/degreetree
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

Create the database first:

```bash
createdb degreetree
```

Then run:

```bash
cd DegreeTree-Backend/courseplanner
./mvnw spring-boot:run
```

On first startup the schema is created automatically and a seeder populates the Villanova CS-major
course catalog and prerequisites (see `CatalogSeeder`). The API starts on `http://localhost:8080`.
Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### Frontend

```bash
cd DegreeTree-Frontend
npm install
npm run dev
```

The dev server runs on `http://localhost:5173` and talks to the backend at `http://localhost:8080`
(configured in `src/services/api.js`).

## API Endpoints

All endpoints are under `/courses`.

| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| GET | `/courses` | List all courses |
| POST | `/courses` | Add a course |
| GET | `/courses/id/{id}` | Get a course by internal id |
| GET | `/courses/code/{courseCode}` | Get a course by course code |
| GET | `/courses/code/{courseCode}/prerequisites` | Get the flat list of prerequisite courses |
| GET | `/courses/code/{courseCode}/prerequisite-tree` | Get the nested AND/OR prerequisite tree |
| POST | `/courses/eligibility` | Given `{ "completed": ["CSC 1051", ...] }`, return the courses now eligible to take |

## Notes

- Eligibility is computed on the backend (`CourseService.checkEligibility`) by evaluating each
  course's prerequisite tree against the set of completed courses.
- The course catalog and prerequisites are seeded automatically on first startup from the Villanova
  CS-major catalog. To reseed, empty the `course` table and restart.
- CORS on the backend allows the frontend on ports 3000, 5173, and 4173.
