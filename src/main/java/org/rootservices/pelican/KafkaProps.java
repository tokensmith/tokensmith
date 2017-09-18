package org.rootservices.pelican;

public enum KafkaProps {
    SERVER ("bootstrap.servers"),
    ACK ("acks"),
    ALL ("all"),
    RETRIES("retries"),
    BATCH_SIZE("batch.size"),
    LINGER ("linger.ms"),
    BUFFER_SIZE ("buffer.memory"),
    KEY_SERIALIZER ("key.serializer"),
    VALUE_SERIALIZER ("value.serializer");

    private String value;

    KafkaProps(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
