ARG TAG_VERSION=openjdk:8-jre

FROM arm32v7/${TAG_VERSION}

ENV env "prod"

WORKDIR /app

ADD ./README.md README.md
ADD ./src/main/resources/static ./src/main/resources/static
ADD ./build/libs/javalinist-1.0-all.jar javalinist-1.0.jar

EXPOSE 8080

CMD ["java", "-jar", "javalinist-1.0.jar"]

HEALTHCHECK --interval=15s --timeout=2s --retries=12 CMD curl --fail localhost:8080/status || exit 1