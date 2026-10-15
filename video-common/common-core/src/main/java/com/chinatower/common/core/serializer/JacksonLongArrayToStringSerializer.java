package com.chinatower.common.core.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author qq on 2023/2/22
 */
public class JacksonLongArrayToStringSerializer extends JsonSerializer<Long[]> {
    @Override
    public void serialize(Long[] longs, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (longs == null) {
            jsonGenerator.writeNull();
            return;
        }
        String[] array = Arrays.stream(longs).map(x -> {
            if (x == null) {
                return null;
            }
            return String.valueOf(x);
        }).toArray(String[]::new);
        jsonGenerator.writeArray(array, 0, array.length);
    }
}
