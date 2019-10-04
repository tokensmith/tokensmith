FROM alpine

RUN  \
    apk update && \
    apk upgrade && \
    apk add --no-cache openjdk11

ARG KAFKA_HOME="/opt/kafka"
ARG KAFKA_VERSION=2.3.0
ARG KAFKA_SCALA_VERSION=2.11
ARG USER_HOME_DIR="/root"

# this could probably go into /usr/local too.
ENV KAFKA_HOME ${KAFKA_HOME}/kafka_${KAFKA_SCALA_VERSION}-${KAFKA_VERSION}

RUN \
    apk update && \
    apk upgrade && \
    apk add bash && \
    apk add bash-doc && \
    apk add bash-completion

RUN \
  wget http://apache.claz.org/kafka/${KAFKA_VERSION}/kafka_${KAFKA_SCALA_VERSION}-${KAFKA_VERSION}.tgz -O /tmp/kafka.tgz && \
  mkdir -p ${KAFKA_HOME} && \
  tar xzf /tmp/kafka.tgz -C /opt/kafka && \
  rm /tmp/kafka.tgz 


WORKDIR /data

CMD ["sh"]
