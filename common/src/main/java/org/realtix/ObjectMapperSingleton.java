package org.realtix;

import com.fasterxml.jackson.databind.ObjectMapper;

public enum ObjectMapperSingleton {

    INSTANCE;
    private final ObjectMapper MAPPER = new ObjectMapper();

    public ObjectMapper mapper() {
        return MAPPER;
    }

}
