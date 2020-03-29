ARG TAG_VERSION=openjdk:8-jre
FROM gradle AS build

WORKDIR /app

ADD . .

RUN gradle build

FROM build AS test

RUN gradle test

FROM arm32v7/${TAG_VERSION} AS prod

ENV env "prod"

WORKDIR /app

COPY --from=build /app/README.md README.md
COPY --from=build /app/build/libs/javalinist-1.0-all.jar javalinist-1.0.jar

EXPOSE 8080

CMD ["java", "-jar", "javalinist-1.0.jar"]

HEALTHCHECK --interval=15s --timeout=2s --retries=12 CMD curl --fail localhost:8080/status || exit 1