package com.chinatower.common.core.utils;

import com.chinatower.common.entity.Result;
import com.google.gson.*;

import java.lang.reflect.Type;

public class GsonDeserialization implements JsonDeserializer<Result> {

    @Override
    public Result deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Result result = Result.success("success");
        if (jsonObject.has("code")) {
            result.setCode(jsonObject.get("code").getAsInt());
        }
        if (jsonObject.has("msg")) {
            result.setMsg(jsonObject.get("msg").getAsString());
        }
        return result;
    }
}
