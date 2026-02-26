# Repository Guidelines

## Project Structure & Module Organization
RepoExpress is a Maven multi-module Java library. The root `pom.xml` aggregates:

- `common/`: shared repository abstractions, adapters, domain helpers, and utilities.
- `mongodb/`: MongoDB/Morphia-backed repositories.
- `cassandra/`: Cassandra-backed repositories.
- `redis/`: Redis/Jedis/JOhm-backed repositories.

Standard Maven layout is used in most modules (`src/main/java`, `src/test/java`). The Redis module is an exception and uses `redis/src/java` (configured in `redis/pom.xml`).

## Build, Test, and Development Commands
- `mvn compile`: compile all modules from the repo root.
- `mvn test`: run unit tests across modules.
- `mvn package`: build jars for all modules.
- `mvn -pl common test`: run tests only for `common`.
- `mvn -pl mongodb test`: run MongoDB module tests (some tests may require local services or be ignored).
- `mvn -DskipTests package`: build quickly without tests.

Use Java 8 for local development (`maven-compiler-plugin` targets `1.8`).

## Coding Style & Naming Conventions
Follow the existing Java style in each module:

- Use tabs for indentation (as seen in current source files).
- Keep package names lowercase under `com.strategicgains.repoexpress...`.
- Use `PascalCase` for classes, `camelCase` for methods/fields, and `UPPER_SNAKE_CASE` for constants.
- Match existing naming patterns such as `*Repository`, `*Adapter`, `*Exception`, and `*Test`.

No formatter/linter is enforced in the build; consistency with surrounding code is expected.

## Testing Guidelines
Tests use JUnit 4 (`org.junit.Test`). Place tests under the module test source tree and name them `*Test.java`.

Prefer fast unit tests in `common/`. Database-backed tests should clearly document prerequisites (for example, local MongoDB config) and should be disabled/isolated when they are not self-contained. Example: `mongodb/src/test/java/.../DateFilteringTest.java` is currently `@Ignore`.

## Commit & Pull Request Guidelines
Git history shows short, imperative commit messages (for example, `Renamed ...`, `Refactored ...`, `Fixed ...`). Keep commits focused and descriptive.

For pull requests, include:

- a concise summary of behavior changes,
- affected modules (`common`, `mongodb`, `cassandra`, `redis`),
- test coverage/run notes (`mvn test`, module-specific tests),
- linked issues (if applicable).
