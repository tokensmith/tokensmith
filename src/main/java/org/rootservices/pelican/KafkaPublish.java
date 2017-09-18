package org.rootservices.pelican;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Component
public class KafkaPublish implements Publish {
    Properties properties;
    ObjectMapper objectMapper;

    @Autowired
    public KafkaPublish(Properties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public void send(String topic, Map<String, String> msg) {
        JsonNode jsonValue = objectMapper.valueToTree(msg);
        Producer<String, JsonNode> producer = new KafkaProducer<>(properties);
        producer.send(new ProducerRecord<>(topic, jsonValue));
        producer.close();
    }
}
