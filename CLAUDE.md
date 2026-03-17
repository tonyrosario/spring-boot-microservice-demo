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

## Default operating mode

- Minimize context and file reads
- Do NOT scan the repo by default
- Open only files required for the current task
- Prefer small, focused diffs touching the fewest files possible
- If unsure, ask up to 3 targeted questions before reading broadly
- Never modify files outside the project root
- Never modify `BACKLOG.md` entries that already exist — append only

## Planning mode

- Active tasks live in `PLANS.md` using milestone-scoped IDs: `M1-001`, `M2-001`
- Each task has a status: `TODO`, `IN PROGRESS`, `DONE`, `BLOCKED`
- When starting a task, update its status to `IN PROGRESS` in `PLANS.md`
- When completing a task, update its status to `DONE` and add a one-line completion note
- Deferred ideas go in `BACKLOG.md` under the appropriate priority section
- If priority is unclear, append under **Medium Priority** in `BACKLOG.md`
- Update `docs/PROGRESS.md` at the end of every session

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

## Testing standards (hard rules)

- **Test behavior, not implementation** — test public APIs and observable outcomes
- **One behavior per test** — focused, independent, no shared mutable state
- **Descriptive test names** — use `@DisplayName` for readability
    - Good: `"returns 404 when product SKU does not exist"`
    - Bad: `"testFindBySku"`
- **Organize with nested `@Nested` classes** mirroring method or scenario structure
- **Builder pattern for test data** — use Lombok `@Builder` on entities, create
  static factory helpers like `ProductTestFactory.aProduct()` with sensible defaults
- **Reset mocks between tests** — use `@ExtendWith(MockitoExtension.class)`
  and `@BeforeEach` to reset shared state
- **Coverage requirements (enforced in CI):**
    - ≥ 80% line coverage (JaCoCo)
    - ≥ 70% mutation score (PIT)
    - Run `./gradlew check` before finalizing any task

## Code standards (hard rules)

- **No wildcard imports** — enforced by Checkstyle, zero exceptions
- **Money** — always use `MonetaryAmount` (JSR 354 / Moneta) — never raw `BigDecimal` for prices
- **Timestamps** — always use `Instant`, never `LocalDateTime` or `Date`
- **Soft delete** — never hard delete products, use `active` flag
- **Validation** — Bean Validation annotations on DTOs, not on JPA entities
- **Error handling** — all exceptions handled in `GlobalExceptionHandler` (`@RestControllerAdvice`)
- **No raw types** — use generics with explicit type parameters
- **Small methods** — if a method needs a comment to explain what it does, break it up
- **Explicit names** — no single-letter variables outside loop counters
- **`DECIMAL(19,4)`** for monetary amounts in the database schema
- **`VARCHAR(3)`** for ISO 4217 currency codes

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

## Milestones reference

| Milestone | Focus | State file |
|-----------|-------|------------|
| M1 | Core API | `docs/PROGRESS.md` |
| M2 | Test coverage | `docs/PROGRESS.md` |
| M3 | CI/CD pipeline | `docs/PROGRESS.md` |
| M4 | Production readiness | `docs/PROGRESS.md` |
| M5 | Polish and portfolio signal | `docs/PROGRESS.md` |

Full milestone definitions live in `PLANS.md`.

## Restrictions

- Never read or modify files inside `build/`, `.gradle/`, or `gradle/wrapper/`
- Never modify `BACKLOG.md` existing entries — append only
- All file I/O must stay within the project root
- Never commit secrets, `.env` files, or credentials of any kind
- Never use `System.out.println` — use SLF4J logger only
- Never expose JPA entities through REST responses
- Never hard-delete products — soft delete only via `active = false`