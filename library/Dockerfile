# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Сначала копируем только pom.xml — Docker закеширует зависимости
# и не будет их качать при каждом изменении кода
COPY pom.xml .
RUN ./mvnw dependency:go-offline -q 2>/dev/null || true

COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Докачиваем зависимости через mvnw
RUN ./mvnw dependency:go-offline -q

# Копируем исходники и собираем JAR (без запуска тестов — они для CI)
COPY src ./src
RUN ./mvnw package -DskipTests -q

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

# Создаём непривилегированного пользователя
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
USER appuser

# Копируем только собранный JAR — образ остаётся маленьким
COPY --from=builder /app/target/book-management-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
