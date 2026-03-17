# PROGRESS.md — Product Catalog Service

## How to use this file
- Update at the end of every Claude Code session
- Record the last known good state — what compiles, what passes tests
- Never mark a milestone COMPLETE unless `./gradlew check` passes clean
- If a session ends mid-task, record the exact stopping point and any
  known issues so the next session can orient immediately

---

## Current Milestone
**M1 — Core API**

## Current Status
**IN PROGRESS**

## Last Known Good State
- `./gradlew build` — passes clean
- `./gradlew test` — 1 default context load test passes
- Checkstyle — configured, no violations

## What Has Been Built

### Completed
- [x] Spring Boot 3.5.x project scaffolded via start.spring.io
- [x] Gradle Groovy DSL, Java 21, Eclipse Temurin aarch64
- [x] Base package renamed from `com.demo.demo` to `com.demo.productcatalog`
- [x] `Product` JPA entity with:
    - Lombok (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`)
    - `@Builder.Default` on `active` field
    - `@PrePersist` / `@PreUpdate` audit timestamps using `Instant`
    - Bean Validation annotations
- [x] `ProductRepository` interface extending `JpaRepository` with soft-delete
  aware queries
- [x] Checkstyle configured — no wildcard imports enforced
- [x] `build.gradle` — `useJUnitPlatform()` configured
- [x] Conventional Commits with scope adopted

### Not Started
- [ ] M1-001 — Add Moneta dependency and create MoneyEmbeddable
- [ ] M1-002 — Refactor Product entity to use MoneyEmbeddable
- [ ] M1-003 — Create ProductRequest DTO
- [ ] M1-004 — Create ProductResponse DTO
- [ ] M1-005 — Create ProductService
- [ ] M1-006 — Create custom exceptions
- [ ] M1-007 — Create GlobalExceptionHandler
- [ ] M1-008 — Create ProductController
- [ ] M1-009 — Configure application.properties for H2 dev database
- [ ] M1-010 — Smoke test — application boots and endpoints reachable

---

## Known Issues / Decisions Pending
- `Product.java` still uses `BigDecimal price` — will be replaced by
  `MoneyEmbeddable` in M1-001 and M1-002
- Bean Validation annotations currently on entity — should migrate to
  `ProductRequest` DTO as part of M1-003 (controllers validate DTOs,
  not entities)

---

## Milestone History

| Milestone | Status | Completed |
|-----------|--------|-----------|
| M1 — Core API | IN PROGRESS | — |
| M2 — Test Coverage | TODO | — |
| M3 — CI/CD Pipeline | TODO | — |
| M4 — Production Readiness | TODO | — |
| M5 — Polish and Portfolio Signal | TODO | — |

---

## Session Log

| Date | What Was Done | Stopping Point |
|------|---------------|----------------|
| 2026-03-17 | Project scaffolded, Product entity, ProductRepository, Checkstyle | Ready for M1-001 |