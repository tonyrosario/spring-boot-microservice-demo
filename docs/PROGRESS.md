# PROGRESS.md — Product Catalog Service

## How to use this file
- Update at the end of every Claude Code session
- Record the last known good state — what compiles, what passes tests
- Never mark a milestone COMPLETE unless `./gradlew check` passes clean
- If a session ends mid-task, record the exact stopping point and any
  known issues so the next session can orient immediately

---

## Current Milestone
**M2 — Test Coverage**

## Current Status
**READY TO START**

## Last Known Good State
- `./gradlew build` — passes clean
- `./gradlew check` — all tests pass, Checkstyle clean, no violations
- Spring Boot **4.0.3**, Java 21
- All M1 tasks committed, full CRUD API functional with H2 in-memory database

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

### Not Started — M2 (Test Coverage)
- [ ] M2-001 — Add JaCoCo plugin and enforce 80% line coverage
- [ ] M2-002 — Add PIT mutation testing plugin and enforce 70% score
- [ ] M2-003 — ProductService unit tests — happy path
- [ ] M2-004 — ProductService unit tests — edge cases and exceptions
- [ ] M2-005 — ProductController integration tests via MockMvc
- [ ] M2-006 — ProductRepository slice tests via @DataJpaTest

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
| M2 — Test Coverage | TODO | — |
| M3 — CI/CD Pipeline | TODO | — |
| M4 — Production Readiness | TODO | — |
| M5 — Polish and Portfolio Signal | TODO | — |

---

## Session Log

| Date | What Was Done | Stopping Point |
|------|---------------|----------------|
| 2026-03-17 | Project scaffolded, Product entity, ProductRepository, Checkstyle | Ready for M1-001 |
| 2026-03-17 | Completed all M1 tasks (M1-001 through M1-010); full CRUD API live on H2 | Ready for M2-001 |
