# ARCHITECTURE.md — Product Catalog Service

## Purpose
This file records architectural decisions made during the build of the
Product Catalog Service. Each decision includes the context, the options
considered, and the rationale. This is the reference for technical
interview discussions and for any agent or engineer picking up the project.

---

## Decision Log

### ADR-001 — Build tool: Gradle over Maven
**Date:** 2026-03-17  
**Status:** Decided

**Context:**
Project requires a modern Java build tool. Both Gradle and Maven are
widely supported by Spring Boot 3.5.x.

**Options considered:**
- Maven — XML-based, verbose, dominant in legacy enterprise (JPM Chase)
- Gradle Groovy DSL — concise, flexible, modern default for greenfield projects
- Gradle Kotlin DSL — type-safe, IDE-friendly, more verbose than Groovy

**Decision:** Gradle Groovy DSL

**Rationale:**
Gradle is the modern standard for greenfield Java projects. Groovy DSL
is more readable than Kotlin DSL for a portfolio project where clarity
matters. Maven compatibility is documented via a `maven` branch in M5-004
to demonstrate knowledge of both tools without burning a pin slot on a
near-duplicate repo.

---

### ADR-002 — Java version: Java 21 LTS
**Date:** 2026-03-17  
**Status:** Decided

**Context:**
Spring Boot 3.5.x requires Java 17 minimum. Java 21 is the current LTS
release and is recommended by Spring for Boot 3.x projects.

**Options considered:**
- Java 17 — minimum supported, widely deployed
- Java 21 — current LTS, virtual threads (Project Loom), pattern matching
- Java 24 — latest, not LTS, too cutting edge for a portfolio demo

**Decision:** Java 21 LTS (Eclipse Temurin 21.0.9 aarch64)

**Rationale:**
Java 21 is the production-standard choice for new Spring Boot 3.x
projects in 2026. Virtual threads are available if needed. LTS status
means employers recognize it as a safe, production-viable choice.

---

### ADR-003 — Monetary values: JSR 354 Moneta over raw BigDecimal
**Date:** 2026-03-17  
**Status:** Decided

**Context:**
Product prices require precise monetary representation with currency
awareness. The service is multi-currency from day one.

**Options considered:**
- `BigDecimal` — built-in, no dependencies, widely understood but
  currency-unaware
- Joda Money — battle-tested, predates JSR 354, effectively superseded
- JSR 354 (Moneta) — Java Money standard API, currency-safe arithmetic,
  `MonetaryAmount` interface, Moneta as reference implementation

**Decision:** JSR 354 with Moneta reference implementation

**Rationale:**
JSR 354 is the Java standard for monetary values. Using `MonetaryAmount`
instead of raw `BigDecimal` signals domain modeling awareness — a price
is not just a number, it has a currency. Currency-safe arithmetic prevents
accidental cross-currency operations at compile time. The `@Embeddable`
persistence pattern required to map `MonetaryAmount` to JPA is a concrete
interview talking point demonstrating JPA value object knowledge.

**Persistence approach:**
Two-column `@Embeddable` via `MoneyEmbeddable`:
- `price_amount DECIMAL(19,4)` — 4 decimal places for currency precision
- `price_currency VARCHAR(3)` — ISO 4217 currency code

`DECIMAL(19,4)` chosen over `DECIMAL(10,2)` because some currencies
have no decimal places (JPY) and precision requirements vary by market.

---

### ADR-004 — Soft delete over hard delete
**Date:** 2026-03-17  
**Status:** Decided

**Context:**
Products need to be removable from the catalog without losing historical
data for order history, reporting, and audit purposes.

**Options considered:**
- Hard delete — `DELETE FROM products WHERE id = ?` — simple but
  destroys data permanently
- Soft delete via `active` flag — `UPDATE products SET active = false`
  — data preserved, queryable, reversible

**Decision:** Soft delete via `active boolean` field

**Rationale:**
In any real e-commerce system, products referenced by historical orders
must never be deleted. Soft delete preserves referential integrity,
supports audit trails, and allows reactivation. All repository queries
are `active = true` scoped by default. Hard delete is never called.

---

### ADR-005 — Timestamps: Instant over LocalDateTime
**Date:** 2026-03-17  
**Status:** Decided

**Context:**
Entity audit fields `createdAt` and `updatedAt` require a timestamp type.

**Options considered:**
- `java.util.Date` — legacy, mutable, avoid
- `LocalDateTime` — no timezone information, ambiguous in distributed systems
- `Instant` — UTC epoch-based, timezone-safe, correct for distributed systems

**Decision:** `Instant`

**Rationale:**
`LocalDateTime` has no timezone context, which causes silent bugs in
distributed systems where servers may be in different timezones. `Instant`
is always UTC, always unambiguous, and maps cleanly to `TIMESTAMP WITH
TIME ZONE` in PostgreSQL. This is the correct choice for any cloud-native
service.

---

### ADR-006 — DTO separation: request and response objects
**Date:** 2026-03-17  
**Status:** Decided

**Context:**
REST endpoints need to accept input and return output. The question is
whether to use JPA entities directly or separate DTO objects.

**Options considered:**
- Expose JPA entities directly — simple, less code, widely considered
  an anti-pattern
- Single DTO for both input and output — reduces class count but mixes
  validation concerns with response shaping
- Separate `ProductRequest` and `ProductResponse` DTOs — more classes
  but clean separation of concerns

**Decision:** Separate `ProductRequest` and `ProductResponse` DTOs

**Rationale:**
Exposing JPA entities through REST responses leaks persistence concerns
into the API contract, creates circular serialization issues with
bidirectional relationships, and makes it impossible to shape responses
independently of the schema. `ProductRequest` carries Bean Validation
annotations. `ProductResponse` carries only the fields the API consumer
needs. The service layer maps between them — entities never cross the
service boundary.

---

### ADR-007 — Constructor injection over field injection
**Date:** 2026-03-17  
**Status:** Decided

**Context:**
Spring beans require dependency injection. Three styles are available:
field injection (`@Autowired`), setter injection, and constructor injection.

**Options considered:**
- Field injection — concise but hides dependencies, breaks without
  Spring context, untestable without reflection
- Setter injection — allows optional dependencies, rarely needed
- Constructor injection — explicit, testable, immutable dependencies,
  Spring Boot default recommendation

**Decision:** Constructor injection only

**Rationale:**
Constructor injection makes dependencies explicit, allows the class to
be instantiated in unit tests without a Spring context, and enables
`final` fields which prevent accidental reassignment. `@Autowired` on
fields is considered a code smell in modern Spring development and will
be flagged by SonarLint.

---

## Pending Decisions

| ID | Topic | Target Milestone |
|----|-------|-----------------|
| ADR-008 | Flyway vs Liquibase for schema migrations | M4 |
| ADR-009 | PostgreSQL datasource connection pooling (HikariCP config) | M4 |
| ADR-010 | API versioning strategy (`/api/v1/` vs header-based) | M5 |