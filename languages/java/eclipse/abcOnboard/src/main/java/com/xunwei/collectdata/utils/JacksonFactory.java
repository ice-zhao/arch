package com.xunwei.collectdata.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JacksonFactory {
    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if(objectMapper == null)
            objectMapper = new ObjectMapper();
        return objectMapper;
    }

    public static JsonNode findJsonNode(String json, String jsonPtr) throws IOException {
        objectMapper = getObjectMapper();
        JsonNode  rootNode = objectMapper.readTree(json);
        JsonNode node = rootNode.at(jsonPtr);
        return node;
    }
}
