# CLAUDE.md — Product Catalog Service

## Project map (read first)

- Single-module Gradle project (Groovy DSL)
- Base package: `com.demo.productcatalog`
- Key commands:
    - `./gradlew build` — compile and package
    - `./gradlew test` — run unit and integration tests
    - `./gradlew check` — run tests + Checkstyle
    - `./gradlew pitest` — mutation testing (PIT) — added in M3
    - `./gradlew jacocoTestReport` — generate coverage report — added in M3
- Generated/output directories (never modify):
    - `build/`
    - `build/reports/tests/`
    - `build/reports/jacoco/`
    - `build/reports/pitest/`
- Active tasks: `PLANS.md` — milestone-scoped task registry
- Deferred ideas: `BACKLOG.md` — append-only, never rewrite existing entries
- Milestone state: `docs/PROGRESS.md` — current milestone, last known good state
- Architecture decisions: `docs/ARCHITECTURE.md` — decision log

---

## Default operating mode

- Minimize context and file reads
- Do NOT scan the repo by default
- Open only files required for the current task
- Prefer small, focused diffs touching the fewest files possible
- If unsure, ask up to 3 targeted questions before reading broadly
- Never modify files outside the project root
- Never modify `BACKLOG.md` entries that already exist — append only

---

## Planning mode

- Active tasks live in `PLANS.md` using milestone-scoped IDs: `M1-001`, `M2-001`
- Each task has a status: `TODO`, `IN PROGRESS`, `DONE`, `BLOCKED`
- When starting a task, update its status to `IN PROGRESS` in `PLANS.md`
- When completing a task, update its status to `DONE` and add a one-line completion note
- Deferred ideas go in `BACKLOG.md` under the appropriate priority section
- If priority is unclear, append under **Medium Priority** in `BACKLOG.md`
- Update `docs/PROGRESS.md` at the end of every session

---

## Package structure and layer rules

```
src/main/java/com/demo/productcatalog/
├── domain/          ← JPA entities and @Embeddable value objects only
├── repository/      ← Spring Data JPA interfaces only
├── service/         ← business logic, validation, orchestration
├── controller/      ← REST endpoints, request mapping, response mapping
└── dto/             ← request and response objects, no JPA annotations
```

**Hard rules:**

- Controllers never call repositories directly — always go through service layer
- JPA entities never leave the service layer — always map to DTOs before returning
- DTOs never carry JPA annotations (`@Entity`, `@Column`, etc.)
- Domain objects never import from `controller` or `dto` packages
- `MoneyEmbeddable` lives in `domain/` — it is a value object, not a DTO

---

## Implementation mode (TDD)

Work in tight Red-Green-Refactor loops:

1. Identify the minimal file(s) needed
2. Write a failing test describing the desired behavior
3. Run the test — confirm it fails (Red)
4. Implement the minimal code to make it pass (Green)
5. Refactor while tests stay green
6. Run `./gradlew check` — must pass before committing
7. Summarize changes and suggest the commit message

Never write production code without a failing test first.
Never paste large stack traces — summarize and reference the failing test name.

---

## Testing standards (hard rules)

### File size

- Maximum 200 lines per test file including imports
- At 150 lines, stop and split into a second file suffixed by behavior
  group: e.g. `ProductServiceValidationTest`, `ProductServicePersistenceTest`
- Never combine tests for more than one production class in one file

### Naming

- Method names follow the pattern: `should_[expectedOutcome]_when_[condition]`
    - `should_returnProduct_when_skuExists()`
    - `should_throwNotFoundException_when_skuIsUnknown()`
    - `should_return400_when_priceIsNegative()`
- `@DisplayName` accompanies every `@Test` and mirrors the method name in
  plain English:
    - `@DisplayName("returns product when SKU exists")`
    - `@DisplayName("throws NotFoundException when SKU is unknown")`
- No generic names: `testSuccess`, `test1`, `happyPath`, `verifyMethod`
- These names are the living documentation — write them so a non-engineer
  can read the test report and understand system behavior

### Unit test annotation stacks (use exactly these per layer)

These rules apply to all files in the `test/` source set that are **not**
annotated with `@SpringBootTest`. `@SpringBootTest` is reserved exclusively
for integration tests — see the Integration tests section below.

| Layer         | Annotation stack                                                |
|---------------|-----------------------------------------------------------------|
| `domain/`     | `@Test` only — zero Spring, zero Mockito                        |
| `service/`    | `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`  |
| `controller/` | `@WebMvcTest(XyzController.class)` + `@MockitoBean` on the service |
| `dto/`        | `Validation.buildDefaultValidatorFactory()` — no Spring context |
| `repository/` | `@DataJpaTest` — test only custom `@Query` or derived methods   |

### Integration tests (reserved — do not create until M4)

- Full-context tests live in a separate `integrationTest/` Gradle source set
- Naming suffix: `IT` — e.g. `ProductControllerIT`, `ProductRepositoryIT`
- Use `@SpringBootTest` + Testcontainers for database-backed scenarios
- `@WebMvcTest` is still preferred over `@SpringBootTest` for controller
  integration tests unless the full application context is explicitly required
- Do not create any `IT` files until the milestone plan explicitly calls for them

> **Exception:** `ProductCatalogServiceApplicationTests` is permitted to use
> `@SpringBootTest` in the `test/` source set. It is a context-load smoke
> test and will be migrated to `integrationTest/` in M4.

### Test structure

- `@Nested` class per scenario group — 3 or more tests sharing a setup
  belong in a nested class:

```java

@Nested
class WhenProductExists { ...
}

@Nested
class WhenProductDoesNotExist { ...
}
```

- Constants and fixtures declared at class top — never scattered inline
- AAA layout with a blank line between Arrange / Act / Assert
- One logical assertion per test (AssertJ fluent chains count as one)

### Test data

- Always use `ProductTestFactory.aProduct()` and similar factory helpers —
  never construct entities inline with `new Product(...)`
- Override only the field(s) directly relevant to the test under construction
- See **Test utilities** section below for factory location and contract

### Assertion rules

- Use AssertJ (`assertThat`) exclusively — never JUnit `assertEquals`
- Exception tests must use the full chain:
  `assertThatThrownBy(...).isInstanceOf(...).hasMessageContaining(...)`
- Never rely solely on `verify()` — always assert a return value or a
  visible state change in addition to any interaction verification

### Forbidden patterns in unit tests (any occurrence = automatic FAIL)

> These rules apply to the `test/` source set only. `@SpringBootTest` and
> Testcontainers are valid and expected in the `integrationTest/` source set
> starting in M4.

- `@SpringBootTest` — forbidden in unit tests
- `Thread.sleep()` or any timing-dependent assertion
- Tests for getters, setters, or Lombok-generated methods
- `verify()` as the only assertion in a test
- Wildcard imports (also enforced by Checkstyle)
- Inline entity construction (`new Product(...)`) — use factory helpers

### Coverage requirements (enforced in CI)

- ≥ 80% line coverage (JaCoCo)
- ≥ 70% mutation score (PIT)
- Run `./gradlew check` before finalizing any task

### Self-evaluation gate

Before marking any test task `DONE` in `PLANS.md`, self-evaluate and
report each item explicitly:

```
[ ] File is under 200 lines (including imports)
[ ] Every @Test method follows should_[outcome]_when_[condition]
[ ] @DisplayName present on every @Test method
[ ] Correct layer annotation stack used — no @SpringBootTest in unit tests
[ ] ProductTestFactory used for all entity construction
[ ] Happy path covered
[ ] Primary failure / not-found case covered
[ ] At least one edge case covered (null, blank, boundary, duplicate)
[ ] All assertions use AssertJ
[ ] No forbidden patterns present
[ ] ./gradlew check passes
```

---

## Test utilities

### ProductTestFactory

- **Location:** `src/test/java/com/demo/productcatalog/util/ProductTestFactory.java`
- **Entry point:** `ProductTestFactory.aProduct()` returns a `Product.builder()`
  pre-populated with valid sensible defaults:
    - Non-null, non-blank SKU
    - Valid `MonetaryAmount` (positive, USD)
    - `active = true`
    - Non-null `Instant` timestamps
- Add new static factory methods here as new entities are introduced
- Never duplicate entity construction logic across test classes — if you
  find yourself building the same object in two test files, it belongs here

---

## Code standards (hard rules)

- **No wildcard imports** — enforced by Checkstyle, zero exceptions
- **Money** — always use `MonetaryAmount` (JSR 354 / Moneta) — never raw
  `BigDecimal` for prices
- **Timestamps** — always use `Instant`, never `LocalDateTime` or `Date`
- **Soft delete** — never hard delete products, use `active` flag
- **Validation** — Bean Validation annotations on DTOs, not on JPA entities
- **Error handling** — all exceptions handled in `GlobalExceptionHandler`
  (`@RestControllerAdvice`)
- **No raw types** — use generics with explicit type parameters
- **Small methods** — if a method needs a comment to explain what it does,
  break it up
- **Explicit names** — no single-letter variables outside loop counters
- **`DECIMAL(19,4)`** for monetary amounts in the database schema
- **`VARCHAR(3)`** for ISO 4217 currency codes

---

## Commit messages

- Use Conventional Commits with scope
- Format: `type(scope): description`
- Scopes: `domain`, `repository`, `service`, `controller`, `dto`, `test`,
  `ci`, `gradle`, `checkstyle`, `docker`, `flyway`, `docs`
- Keep subject under 72 characters
- Do not restate the full plan in the commit message
- Examples:
    - `feat(service): add ProductService.create() with duplicate SKU validation`
    - `test(service): add unit tests for ProductService happy path and edge cases`
    - `refactor(domain): replace BigDecimal price with MonetaryAmount via @Embedded`
    - `ci(github-actions): add CI workflow with test and coverage gates`

---

## Milestones reference

| Milestone | Focus                       | State file         |
|-----------|-----------------------------|--------------------|
| M1        | Core API                    | `docs/PROGRESS.md` |
| M2        | Test coverage               | `docs/PROGRESS.md` |
| M3        | CI/CD pipeline              | `docs/PROGRESS.md` |
| M4        | Production readiness        | `docs/PROGRESS.md` |
| M5        | Polish and portfolio signal | `docs/PROGRESS.md` |

Full milestone definitions live in `PLANS.md`.

---

## Restrictions

- Never read or modify files inside `build/`, `.gradle/`, or `gradle/wrapper/`
- Never modify `BACKLOG.md` existing entries — append only
- All file I/O must stay within the project root
- Never commit secrets, `.env` files, or credentials of any kind
- Never use `System.out.println` — use SLF4J logger only
- Never expose JPA entities through REST responses
- Never hard-delete products — soft delete only via `active = false`