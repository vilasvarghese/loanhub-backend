# loanhub-backend

Spring Boot 3.2.4 REST API for the LoanHub digital lending portal. Java 21 · Maven · PostgreSQL · self-issued HS256 JWT auth.

Part of the LoanHub polyrepo:
[`loanhub-backend`](https://github.com/raj-pro/loanhub-backend) ·
[`loanhub-frontend`](https://github.com/raj-pro/loanhub-frontend) ·
[`loanhub-infra`](https://github.com/raj-pro/loanhub-infra) ·
[`loanhub-gitops`](https://github.com/raj-pro/loanhub-gitops)

## Build & test

> ⚠️ Build with **JDK 21** (Temurin). Newer JDKs break this stack (Lombok, Mockito,
> and Spring's lambda-converter resolution all fail on JDK 23+). CI pins Temurin 21.

```sh
export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"  # macOS/Homebrew
mvn -B verify          # compile + unit/integration tests
mvn -DskipTests package # build the runnable jar
```

## Container

Multi-stage [`Dockerfile`](Dockerfile): Maven (Temurin 21) build stage → slim `eclipse-temurin:21-jre` runtime.

```sh
docker build -t loanhub-backend .
```

## Configuration

Externalized via environment variables (12-factor). Key ones:

| Variable | Purpose |
|----------|---------|
| `SPRING_DATASOURCE_URL` / `_USERNAME` / `_PASSWORD` | PostgreSQL connection |
| `APP_CORS_ALLOWED_ORIGINS` | Allowed browser origins |
| `JWT_SECRET` | HS256 signing secret (≥ 32 chars) |

In Kubernetes these come from a ConfigMap (non-secret) + Secret (DB password) — see `loanhub-gitops`.

## Quality gates

- **CI** ([`.github/workflows/ci.yml`](.github/workflows/ci.yml)): build & test on JDK 21, plus Checkstyle.
- **Pre-commit hook**: `git config core.hooksPath .githooks` to run Checkstyle locally before each commit.
