FROM adoptopenjdk/openjdk12:alpine

RUN  \
    apk update && \
    apk upgrade && \
    apk add --no-cache postgresql-client && \
    apk add --no-cache bash && \
    mkdir -p /application

COPY . /application/
WORKDIR /application

CMD ["bash", "./sh/migrate.sh"]