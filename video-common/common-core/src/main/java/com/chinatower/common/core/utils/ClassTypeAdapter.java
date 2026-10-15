package com.chinatower.common.core.utils;

import com.google.gson.*;
import java.lang.reflect.Type;

public class ClassTypeAdapter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

    @Override
    public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
        // 序列化时存储类的全限定名
        return new JsonPrimitive(src.getName());
    }

    @Override
    public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            // 反序列化时根据类名加载类
            String className = json.getAsString();
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }
}
