# PROGRESS.md — Product Catalog Service

## How to use this file
- Update at the end of every Claude Code session
- Record the last known good state — what compiles, what passes tests
- Never mark a milestone COMPLETE unless `./gradlew check` passes clean
- If a session ends mid-task, record the exact stopping point and any
  known issues so the next session can orient immediately

---

## Current Milestone
**M4 — Production Readiness**

## Current Status
**READY TO START**

## Last Known Good State
- `./gradlew build` — passes clean
- `./gradlew check` — all tests pass, Checkstyle clean, JaCoCo ≥ 80% line coverage gate passes
- `./gradlew jacocoTestReport` — generates HTML + XML in `build/reports/jacoco/`
- `./gradlew pitest` — 83% mutation kill rate, passes 70% threshold
- `./gradlew installGitHooks` — installs pre-commit hook that runs `./gradlew check`
- CI runs on every push and PR to main via `.github/workflows/ci.yml`
- Spring Boot **4.0.3**, Java 21

## What Has Been Built

### Completed — M1 (Core API)
- [x] M1-001 — `org.javamoney:moneta:1.4.4` added; `MoneyEmbeddable` value object
  (`@Embeddable`, `@Getter`, `of()`, `toMonetaryAmount()`)
- [x] M1-002 — `Product` entity refactored to use `@Embedded MoneyEmbeddable price`
  mapped to `price_amount DECIMAL(19,4)` / `price_currency VARCHAR(3)`
- [x] M1-003 — `ProductRequest` DTO with full Bean Validation (`@NotBlank`, `@NotNull`,
  `@DecimalMin`, `@Size`, `@Pattern([A-Z]{3})`)
- [x] M1-004 — `ProductResponse` DTO with all fields and Lombok `@Builder`
- [x] M1-005 — `ProductService` with `create`, `findById`, `findAll`, `update`,
  `deactivate`; maps request→entity→response; never exposes raw entities
- [x] M1-006 — `ProductNotFoundException` and `DuplicateSkuException` in
  `domain/exception/`
- [x] M1-007 — `GlobalExceptionHandler` (`@RestControllerAdvice`) handling 400,
  404, 409, 500
- [x] M1-008 — `ProductController` with full CRUD endpoints under `/api/products`
- [x] M1-009 — `application.properties` configured for H2, H2 console, DDL auto,
  show-sql, Actuator (`/health`, `/info`)
- [x] M1-010 — Smoke tests: context loads, `POST` returns 201, `GET` unknown id
  returns 404, `POST` missing name returns 400

### Completed — M3 (CI/CD Pipeline)
- [x] M3-001 — GitHub Actions CI workflow (`.github/workflows/ci.yml`)
- [x] M3-002 — JaCoCo report published as workflow artifact (14-day retention)
- [x] M3-003 — CI badge in README; Spring Boot version corrected to 4.0
- [x] M3-004 — Dependabot for Gradle + GitHub Actions (weekly)
- [x] M3-005 — `.githooks/pre-commit` + `installGitHooks` Gradle task

### Completed — M2 (Test Coverage)
- [x] M2-001 — JaCoCo plugin; 80% line coverage gate wired to `check`
- [x] M2-002 — PIT plugin (1.19.0-rc.3, Gradle 9 compatible); 70% mutation threshold configured
- [x] M2-003 — `ProductServiceTest` — happy path for all 5 service methods
- [x] M2-004 — `ProductServiceExceptionTest` — duplicate SKU and not-found exception paths
- [x] M2-005 — `ProductControllerTest` (@WebMvcTest, happy path) + `ProductControllerExceptionTest` (400/404/409)
- [x] M2-006 — `ProductRepositoryTest` (@DataJpaTest) — existsBySku, findAllByActiveTrue, findBySkuAndActiveTrue

---

## Known Issues / Decisions Pending
- `Product.java` still carries `@NotBlank` and `@Size` on `name`, `description`,
  and `sku` fields — CLAUDE.md says validation belongs on DTOs, not entities.
  Not addressed in M1 scope; consider cleaning up in M2 or a dedicated refactor task.
- **Spring Boot 4 package change:** `@AutoConfigureMockMvc` moved from
  `org.springframework.boot.test.autoconfigure.web.servlet` to
  `org.springframework.boot.webmvc.test.autoconfigure` — use the new package
  in all M2 MockMvc tests.

---

## Milestone History

| Milestone | Status | Completed |
|-----------|--------|-----------|
| M1 — Core API | COMPLETE | 2026-03-17 |
| M2 — Test Coverage | COMPLETE | 2026-03-19 |
| M3 — CI/CD Pipeline | COMPLETE | 2026-03-19 |
| M4 — Production Readiness | TODO | — |
| M5 — Polish and Portfolio Signal | TODO | — |

---

## Session Log

| Date | What Was Done | Stopping Point |
|------|---------------|----------------|
| 2026-03-17 | Project scaffolded, Product entity, ProductRepository, Checkstyle | Ready for M1-001 |
| 2026-03-17 | Completed all M1 tasks (M1-001 through M1-010); full CRUD API live on H2 | Ready for M2-001 |
| 2026-03-19 | Refactored all tests to CLAUDE.md standards (ProductTestFactory, @WebMvcTest, naming convention, file size split) | Ready for M2 execution |
| 2026-03-19 | Completed all M2 tasks (M2-001 through M2-006); JaCoCo + PIT configured, full test suite passing | Ready for M3-001 |
| 2026-03-19 | Completed all M3 tasks (M3-001 through M3-005); CI workflow, JaCoCo artifact, badge, Dependabot, pre-commit hook | Ready for M4-001 |
