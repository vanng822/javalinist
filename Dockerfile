ARG TAG_VERSION=openjdk:8-jre

FROM arm32v7/${TAG_VERSION}

WORKDIR /app

ADD ./README.md README.md
ADD ./build/libs/javalinist-1.0-all.jar server.jar

EXPOSE 8080

CMD ["java", "-jar", "server.jar"]

HEALTHCHECK --interval=15s --timeout=2s --retries=12 CMD curl --fail localhost:8080/status || exit 1