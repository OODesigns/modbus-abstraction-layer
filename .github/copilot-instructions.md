# Copilot Instructions for Central Auth Service

## Architecture Overview

This is a **Hexagonal Architecture (Ports & Adapters)** authentication service in Java 25 with strict separation:

- **domain/** - Pure business logic, no framework dependencies. Contains `Ports.java` interface definitions
- **application/** - Command handlers orchestrating domain services (e.g., `LoginCommandHandler`)
- **infrastructure/** - Adapters implementing `Ports.*` interfaces (JOOQ, BCrypt, JWT, Bucket4j)

All external dependencies flow through `Ports` interfaces defined in `src/main/java/com/oodesigns/cas/domain/service/Ports.java`.

## Key Patterns

### Value Objects with ValidatedValue Base
All domain values extend `ValidatedValue<T>` with this pattern:
```java
public final class Username extends ValidatedValue<String> {
    private Username(String value) { super(value); }  // private constructor
    public static Username of(String value) { /* validate then construct */ }  // factory method
}
```
Validation happens in `of()` factory methods, NOT constructors. See `Username.java` for an example.

### Fluent Result Pattern (mapTo/orElse)
Results use sealed interfaces with fluent mapping instead of exceptions:
```java
result.mapTo(success -> handleSuccess(success))
      .orElse(failure -> handleFailure(failure));
```
Applied in: `LoginResult`, `RateLimitResult`. Never use `instanceof` checks.

### Sensitive Data Handling
- `Password` and `Credentials` implement `AutoCloseable` - always use try-with-resources
- Char arrays for passwords, cleared after use via `close()`
- `KeyPassword` extends `Password` for keystore secrets

## Testing

### Test Tiers (JUnit Tags)
```bash
./gradlew test                          # Unit tests only (default, excludes integration)
./gradlew integrationTest               # Integration tests (no database)
./gradlew databaseIntegrationTest -PincludeDbTests  # Database tests (requires docker-compose)
```

### Coverage Requirement
**100% line coverage enforced** via JaCoCo (excludes `Ports.java` and `DatabaseContextFactory`).

### Mocking Pattern
Tests use Mockito with `@ExtendWith(MockitoExtension.class)`. Mock all `Ports.*` interfaces in unit tests. See `LoginCommandHandlerTest.java` for an example.

## Database

- **PostgreSQL** via docker-compose with Flyway migrations
- **JOOQ** for type-safe queries - adapters in `infrastructure/adapter/`
- Stored procedures in `api_schema` over `private_schema` data (e.g., `api_schema.find_user_credentials()`)
- Config via `application.properties` with `${ENV_VAR:default}` syntax

## Build Commands

```bash
./gradlew build                    # Compile + unit tests
./gradlew test                     # Unit tests with JaCoCo report
./gradlew jacocoTestCoverageVerification  # Verify 100% coverage
```

## Conventions

- **Records** for DTOs, value objects, and immutable data carriers
- **Sealed interfaces** for result types with exhaustive handling
- **Optional** chaining for null-safe flows (no null returns from public methods)
- **Objects.requireNonNull()** in all constructors for required dependencies
- **Final fields and parameters** everywhere - immutability by default

## Typed Method Boundaries

- Business and application methods accept named classes/records, validated value objects, or command/query objects; do not expose primitive, `String`, raw collection, or enum parameters in these method APIs.
- Only constructors may accept raw primitives, strings, enums, or wire/config values. Validate and convert them immediately into immutable domain/value objects; methods, including parsing/decoding helpers, accept named classes/value objects rather than raw inputs.
- Enums may be fields inside a command or value object, but methods receive that containing object rather than a bare enum.
- Return domain/value objects from business methods instead of unvalidated scalar measurements. Unwrap values only at serialization, persistence, UI, or protocol boundaries.
- Give constrained values explicit types and constructor invariants. For example, an MVHR weekly setpoint must use a 15-30 C value object, while ventilation-only is a distinct mode rather than a numeric sentinel. Test valid boundaries and rejection of invalid inputs at construction.

## No-Throw Method Contract

- Constructors are the only place where invalid object construction may throw (for example, `IllegalArgumentException` for an invalid value object). Validate required fields and invariants before an instance can exist.
- Every non-constructor method returns a declared result type; do not use `void` methods or `throws` clauses in domain/application APIs. Operations with no payload return `Response<Void>` or `CompletableFuture<Response<Void>>`.
- Methods report expected validation, state, and infrastructure failures through `Response<T>` (or `CompletableFuture<Response<T>>`) instead of throwing. Catch and normalize recoverable exceptions at adapter boundaries; do not catch JVM `Error` types.
- Static factories and parsing methods accept typed input objects and return typed results such as `Response<T>`; they do not throw for expected invalid input. Raw external input should be captured by a boundary/config object constructor, then handed to methods as validated objects.
