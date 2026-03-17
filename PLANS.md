# PLANS.md — Product Catalog Service Task Registry

## How to use this file
- Pick the next `TODO` task in the current milestone
- Update status to `IN PROGRESS` before starting
- Update status to `DONE` with a one-line completion note when finished
- Never delete tasks — status changes are the audit trail
- Blocked tasks go to `BLOCKED` with a reason noted inline

---

## Milestone 1 — Core API

**Goal:** Application boots, all CRUD endpoints are reachable, requests and
responses use DTOs, errors are handled globally.

### M1-001 — Add Moneta dependency and create MoneyEmbeddable
**Status:** DONE — added moneta:1.4.4, created MoneyEmbeddable with of()/toMonetaryAmount()
**Scope:** `domain`  
**Files:**
- `build.gradle` — add `org.javamoney:moneta` dependency
- `src/main/java/com/demo/productcatalog/domain/MoneyEmbeddable.java`

**Acceptance criteria:**
- `MoneyEmbeddable` is `@Embeddable` with `amount (BigDecimal)` and
  `currency (String)` fields
- Includes a static factory method `of(MonetaryAmount)` and a `toMonetaryAmount()`
  method
- No wildcard imports
- Project compiles clean

**Suggested commit:**
`feat(domain): add MoneyEmbeddable value object with JSR 354 Moneta dependency`

---

### M1-002 — Refactor Product entity to use MoneyEmbeddable
**Status:** DONE — replaced BigDecimal price with @Embedded MoneyEmbeddable mapped to price_amount/price_currency
**Scope:** `domain`  
**Files:**
- `src/main/java/com/demo/productcatalog/domain/Product.java`

**Acceptance criteria:**
- `BigDecimal price` replaced with `MoneyEmbeddable price` using `@Embedded`
- `@AttributeOverrides` maps to `price_amount DECIMAL(19,4)` and
  `price_currency VARCHAR(3)`
- `@Builder.Default` preserved on `active` field
- All Lombok annotations intact
- No wildcard imports
- Project compiles clean

**Suggested commit:**
`refactor(domain): replace BigDecimal price with MoneyEmbeddable via @Embedded`

---

### M1-003 — Create ProductRequest DTO
**Status:** DONE — created ProductRequest with Bean Validation and Lombok @Builder(toBuilder = true)
**Scope:** `dto`  
**Files:**
- `src/main/java/com/demo/productcatalog/dto/ProductRequest.java`

**Acceptance criteria:**
- Fields: `name`, `description`, `sku`, `priceAmount (BigDecimal)`,
  `priceCurrency (String)`
- Full Bean Validation: `@NotBlank`, `@NotNull`, `@DecimalMin`, `@Size`,
  `@Pattern` for ISO 4217 currency code
- Uses Lombok `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- No JPA annotations
- No wildcard imports

**Suggested commit:**
`feat(dto): add ProductRequest with Bean Validation`

---

### M1-004 — Create ProductResponse DTO
**Status:** TODO  
**Scope:** `dto`  
**Files:**
- `src/main/java/com/demo/productcatalog/dto/ProductResponse.java`

**Acceptance criteria:**
- Fields: `id`, `name`, `description`, `sku`, `priceAmount (BigDecimal)`,
  `priceCurrency (String)`, `active`, `createdAt`, `updatedAt`
- Uses Lombok `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- No JPA annotations
- No wildcard imports

**Suggested commit:**
`feat(dto): add ProductResponse DTO`

---

### M1-005 — Create ProductService
**Status:** TODO  
**Scope:** `service`  
**Files:**
- `src/main/java/com/demo/productcatalog/service/ProductService.java`

**Acceptance criteria:**
- Methods: `create(ProductRequest)`, `findById(Long)`, `findAll()`,
  `update(Long, ProductRequest)`, `deactivate(Long)`
- Maps `ProductRequest` → `Product` entity on input
- Maps `Product` entity → `ProductResponse` on output — never returns raw entity
- Throws `ProductNotFoundException` (to be created) when product not found
- Throws `DuplicateSkuException` (to be created) when SKU already exists on create
- Uses `ProductRepository` — injected via constructor, not `@Autowired` field injection
- No wildcard imports

**Suggested commit:**
`feat(service): add ProductService with CRUD operations and DTO mapping`

---

### M1-006 — Create custom exceptions
**Status:** TODO  
**Scope:** `domain`  
**Files:**
- `src/main/java/com/demo/productcatalog/domain/exception/ProductNotFoundException.java`
- `src/main/java/com/demo/productcatalog/domain/exception/DuplicateSkuException.java`

**Acceptance criteria:**
- `ProductNotFoundException` extends `RuntimeException`, accepts `Long id`
  in constructor, message: `"Product not found with id: {id}"`
- `DuplicateSkuException` extends `RuntimeException`, accepts `String sku`
  in constructor, message: `"Product already exists with SKU: {sku}"`
- No wildcard imports

**Suggested commit:**
`feat(domain): add ProductNotFoundException and DuplicateSkuException`

---

### M1-007 — Create GlobalExceptionHandler
**Status:** TODO  
**Scope:** `controller`  
**Files:**
- `src/main/java/com/demo/productcatalog/controller/GlobalExceptionHandler.java`

**Acceptance criteria:**
- Annotated `@RestControllerAdvice`
- Handles `ProductNotFoundException` → `404 NOT FOUND` with error body
- Handles `DuplicateSkuException` → `409 CONFLICT` with error body
- Handles `MethodArgumentNotValidException` → `400 BAD REQUEST` with
  field-level validation errors
- Handles `Exception` (catch-all) → `500 INTERNAL SERVER ERROR`
- Error response body includes: `status`, `message`, `timestamp`
- No wildcard imports

**Suggested commit:**
`feat(controller): add GlobalExceptionHandler for 400, 404, 409, and 500 responses`

---

### M1-008 — Create ProductController
**Status:** TODO  
**Scope:** `controller`  
**Files:**
- `src/main/java/com/demo/productcatalog/controller/ProductController.java`

**Acceptance criteria:**
- `POST /api/products` → `201 CREATED` with `ProductResponse`
- `GET /api/products/{id}` → `200 OK` with `ProductResponse`
- `GET /api/products` → `200 OK` with `List<ProductResponse>`
- `PUT /api/products/{id}` → `200 OK` with updated `ProductResponse`
- `DELETE /api/products/{id}` → `204 NO CONTENT` (soft delete via deactivate)
- Uses `@Valid` on request body parameters
- Constructor injection only
- No wildcard imports

**Suggested commit:**
`feat(controller): add ProductController with full CRUD endpoints`

---

### M1-009 — Configure application.properties for H2 dev database
**Status:** TODO  
**Scope:** `gradle`  
**Files:**
- `src/main/resources/application.properties`

**Acceptance criteria:**
- H2 in-memory datasource configured
- H2 console enabled at `/h2-console` for local debugging
- `spring.jpa.hibernate.ddl-auto=create-drop`
- `spring.jpa.show-sql=true` for dev visibility
- Actuator endpoints `/health` and `/info` exposed

**Suggested commit:**
`chore(config): configure H2 datasource and Actuator for dev profile`

---

### M1-010 — Smoke test — application boots and endpoints reachable
**Status:** TODO  
**Scope:** `test`  
**Files:**
- `src/test/java/com/demo/productcatalog/ProductCatalogApplicationTests.java`

**Acceptance criteria:**
- Application context loads without errors
- `POST /api/products` accepts a valid request and returns `201`
- `GET /api/products/{id}` returns `404` for unknown id
- `POST /api/products` with missing `name` returns `400`
- `./gradlew test` passes clean

**Suggested commit:**
`test(integration): add smoke tests for application context and core endpoints`

---

## Milestone 2 — Test Coverage

**Goal:** 80% line coverage via JaCoCo, 70% mutation score via PIT,
all enforced in the build.

| ID | Task | Status |
|----|------|--------|
| M2-001 | Add JaCoCo plugin and enforce 80% line coverage | TODO |
| M2-002 | Add PIT mutation testing plugin and enforce 70% score | TODO |
| M2-003 | ProductService unit tests — happy path | TODO |
| M2-004 | ProductService unit tests — edge cases and exceptions | TODO |
| M2-005 | ProductController integration tests via MockMvc | TODO |
| M2-006 | ProductRepository slice tests via @DataJpaTest | TODO |

---

## Milestone 3 — CI/CD Pipeline

**Goal:** Every push and PR runs build, test, coverage, and Checkstyle
via GitHub Actions.

| ID | Task | Status |
|----|------|--------|
| M3-001 | Add GitHub Actions CI workflow | TODO |
| M3-002 | Publish JaCoCo report as workflow artifact | TODO |
| M3-003 | Add build status badge to README | TODO |
| M3-004 | Add Dependabot configuration | TODO |
| M3-005 | Add Husky pre-commit hooks for Checkstyle and tests | TODO |

---

## Milestone 4 — Production Readiness

**Goal:** Service runs in Docker with PostgreSQL, Flyway manages schema,
Spring profiles separate dev from prod.

| ID | Task | Status |
|----|------|--------|
| M4-001 | Add multi-stage Dockerfile (linux/amd64) | TODO |
| M4-002 | Add Docker Compose with PostgreSQL | TODO |
| M4-003 | Add Flyway and V1 migration script | TODO |
| M4-004 | Add Spring prod profile with PostgreSQL datasource | TODO |
| M4-005 | Expose and document Actuator endpoints | TODO |

---

## Milestone 5 — Polish and Portfolio Signal

**Goal:** Repo is interview-ready — documented, versioned, and
demonstrates professional-grade engineering habits.

| ID | Task | Status |
|----|------|--------|
| M5-001 | Add springdoc-openapi and Swagger UI | TODO |
| M5-002 | Write full project README | TODO |
| M5-003 | Add CHANGELOG.md following Keep a Changelog format | TODO |
| M5-004 | Create maven branch with equivalent configuration | TODO |
| M5-005 | Tag v1.0.0 release | TODO |