package xunwei.collectdata.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class JacksonFactory {
    private static ObjectMapper objectMapper;
    private static final SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static ObjectMapper getObjectMapper() {
        if(objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(simpleDateFormat);
        }

        return objectMapper;
    }

    public static JsonNode findJsonNode(String json, String jsonPtr) throws IOException {
        objectMapper = getObjectMapper();
        JsonNode  rootNode = objectMapper.readTree(json);
        JsonNode node = rootNode.at(jsonPtr);
        return node;
    }
}
