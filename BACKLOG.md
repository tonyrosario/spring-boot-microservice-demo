# BACKLOG.md — Product Catalog Service

## How to use this file
- Append only — never rewrite or delete existing entries
- Add new items under the appropriate priority section
- Items here are not scheduled — they move to `PLANS.md` when prioritized
- Date every entry so the history is traceable

---

## High Priority

- [2026-03-17] Add pagination and sorting to `GET /api/products`
  — `Pageable` support via Spring Data, `Page<ProductResponse>` response
- [2026-03-17] Add product search by name and SKU
  — query method or `@Query` in `ProductRepository`

---

## Medium Priority

- [2026-03-17] Add category/tag support to products
  — `@ManyToMany` relationship, filter by category in list endpoint
- [2026-03-17] Add inventory/stock quantity field to Product entity
- [2026-03-17] Add request/response logging via Spring `HandlerInterceptor`
  or filter — structured JSON logs via SLF4J
- [2026-03-17] Add `@CreatedBy` / `@LastModifiedBy` Spring Data auditing
  — requires `AuditorAware` implementation
- [2026-03-17] Explore Go or Rust implementation of the same API
  — parallel repo for language breadth signal on GitHub profile

---

## Low Priority

- [2026-03-17] Add rate limiting via Spring Cloud Gateway or Bucket4j
- [2026-03-17] Add Redis caching for `GET /api/products/{id}`
  — `@Cacheable` with TTL configuration
- [2026-03-17] Explore Testcontainers for integration tests
  — replace H2 with real PostgreSQL in test slice
- [2026-03-17] Add Spring Security with API key authentication
  — header-based, no OAuth complexity for a demo service
- [2026-03-17] Add currency conversion endpoint
  — integrates with an external exchange rate API, showcases
  multi-currency JSR 354 benefits fully
- [2026-03-17] Kubernetes deployment manifests
  — Deployment, Service, ConfigMap, and HorizontalPodAutoscaler
- [2026-03-17] Helm chart for local dev cluster deployment