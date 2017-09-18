package org.rootservices.pelican;

import java.util.Map;

public interface Publish {
    void send(String topic, Map<String, String> msg);
}
