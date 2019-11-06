package net.tokensmith.config;

import net.tokensmith.pelican.config.PelicanAppConfig;

public class GizmoPelicanAppConfig extends PelicanAppConfig {

    public String messageQueueHost() {
        // TODO: use spring profiles.
        return "localhost:9092";
    }
}
