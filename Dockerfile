FROM adoptopenjdk/openjdk12:alpine-slim AS builder
LABEL stage=tokensmith_builder

RUN  \
    apk update && \
    apk upgrade && \
    mkdir -p /application

COPY . /application/

RUN cd /application && \
    ./gradlew clean build -x test

FROM adoptopenjdk/openjdk12:alpine-jre

RUN  \
    apk update && \
    apk upgrade && \
    apk add --no-cache curl && \
    mkdir -p /application && \
    mkdir -p /var/log/application

COPY --from=builder /application/http/build/libs/id-server-0.0.1-SNAPSHOT.war /application/id-server-0.0.1-SNAPSHOT.war

CMD ["java", "-jar", "-Dlog4j2.configurationFile=log4j2-docker.properties", "-DrequestLog=/var/log/application/jetty-yyyy_mm_dd.request.log", "/application/id-server-0.0.1-SNAPSHOT.war"]
