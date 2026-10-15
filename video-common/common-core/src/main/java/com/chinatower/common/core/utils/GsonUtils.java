package com.chinatower.common.core.utils;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * Gson工具类，用于替代Fastjson
 * 提供与Fastjson类似的API，方便迁移
 *
 * @author system
 * @date 2025
 */
public class GsonUtils {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认Gson实例（序列化null值，自定义日期格式）
     */
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()  // 序列化null值，对应Fastjson的WriteMapNullValue
            .setDateFormat(YYYY_MM_DD_HH_MM_SS)  // 日期格式
            .disableHtmlEscaping()  // 禁用HTML转义
            .create();


    private static final Gson SAFE_GSON = new GsonBuilder()
//            .serializeNulls()  // 序列化null值，对应Fastjson的WriteMapNullValue
            .registerTypeAdapter(Class.class, new ClassTypeAdapter())
            .setDateFormat(YYYY_MM_DD_HH_MM_SS)  // 日期格式
            .disableHtmlEscaping()  // 禁用HTML转义
            .registerTypeAdapterFactory(new CycleSafeTypeAdapterFactory())
            .setExclusionStrategies(new DuplicateFieldExclusionStrategy())
            .create();

    /**
     * 可以跳过类中有重复属性的报错
     *
     * @param obj 对象
     * @return jsonStr
     */
    public static String toSafeJson(Object obj) {
        return SAFE_GSON.toJson(obj);
    }

    static class DuplicateFieldExclusionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            // 跳过Class类型的字段
            if (f.getDeclaredType().equals(Class.class)) {
                return true;
            }

            SerializedName annotation = f.getAnnotation(SerializedName.class);
            String fieldName = annotation != null ? annotation.value() : f.getName();
            Class<?> declaringClass = f.getDeclaringClass();

            //1. 首先判断当前字段是否在父类中存在同名字段
            boolean hasSuperClassField = false;
            Class<?> superClass = declaringClass.getSuperclass();
            while (superClass != null && !superClass.equals(Object.class)) {
                //遍历父类的所有字段
                try {
                    Field superField = superClass.getDeclaredField(f.getName());
                    SerializedName superAnnotation = superField.getAnnotation(SerializedName.class);
                    String superFieldName = superAnnotation != null ? superAnnotation.value() : superField.getName();
                    if (superFieldName.equals(fieldName)) {
                        hasSuperClassField = true;
                        break;
                    }
                } catch (NoSuchFieldException ignored) {
                    //父类没有该字段，继续往上找
                }
                superClass = superClass.getSuperclass();
            }

            //2. 再判断当前类内部是否有多个同名字段
            long duplicateCount = Arrays.stream(declaringClass.getDeclaredFields())
                    .filter(fd -> !fd.isSynthetic())
                    .map(fd -> {
                        SerializedName fdAnno = fd.getAnnotation(SerializedName.class);
                        return fdAnno != null ? fdAnno.value() : fd.getName();
                    })
                    .filter(name -> name.equals(fieldName))
                    .distinct()
                    .count();

            //3. 父类存在同名字段 或者 当前类存在多个同名字段时跳过
            return hasSuperClassField || duplicateCount > 1;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

    /**
     * 格式化输出的Gson实例
     */
    private static final Gson PRETTY_GSON = new GsonBuilder()
            .serializeNulls()
            .setDateFormat(YYYY_MM_DD_HH_MM_SS)
            .setPrettyPrinting()  // 格式化输出
            .disableHtmlEscaping()
            .create();

    /**
     * 不序列化null值的Gson实例（默认行为）
     */
    private static final Gson GSON_WITHOUT_NULL = new GsonBuilder()
            .setDateFormat(YYYY_MM_DD_HH_MM_SS)
            .disableHtmlEscaping()
            .create();

    /**
     * 对象转JSON字符串
     * 对应Fastjson: JSON.toJSONString(obj)
     *
     * @param obj 要转换的对象
     * @return JSON字符串
     */
    public static String toJSONString(Object obj) {
        if (obj == null) {
            return null;
        }
//        return GSON.toJson(obj);
        return SAFE_GSON.toJson(obj);
    }

    /**
     * 对象转JSON字符串（不序列化null值）
     *
     * @param obj 要转换的对象
     * @return JSON字符串
     */
    public static String toJSONStringWithoutNull(Object obj) {
        if (obj == null) {
            return null;
        }
        return GSON_WITHOUT_NULL.toJson(obj);
    }

    /**
     * 对象转JSON字符串（格式化输出）
     *
     * @param obj 要转换的对象
     * @return 格式化的JSON字符串
     */
    public static String toJSONStringPretty(Object obj) {
        if (obj == null) {
            return null;
        }
        return PRETTY_GSON.toJson(obj);
    }

    /**
     * JSON字符串转对象
     * 对应Fastjson: JSON.parseObject(json, Class)
     *
     * @param json  JSON字符串
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 转换后的对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return SAFE_GSON.fromJson(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON解析失败: " + json, e);
        }
    }

    /**
     * JSON字符串转List
     * 对应Fastjson: JSON.parseArray(json, Class)
     *
     * @param json  JSON字符串
     * @param clazz List元素类型
     * @param <T>   泛型类型
     * @return 转换后的List
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            Type type = TypeToken.getParameterized(List.class, clazz).getType();
            return SAFE_GSON.fromJson(json, type);
        } catch (Exception e) {
            throw new RuntimeException("JSON数组解析失败: " + json, e);
        }
    }

    /**
     * JSON字符串转泛型对象
     * 对应Fastjson: JSON.parseObject(json, new TypeReference<Type>(){})
     *
     * @param json JSON字符串
     * @param type 目标类型（使用TypeToken获取）
     * @param <T>  泛型类型
     * @return 转换后的对象
     */
    public static <T> T parseObject(String json, Type type) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return SAFE_GSON.fromJson(json, type);
        } catch (Exception e) {
            throw new RuntimeException("JSON解析失败: " + json, e);
        }
    }

    /**
     * 解析为JsonObject
     * 对应Fastjson: JSON.parseObject(json) 或 new JSONObject()
     *
     * @param json JSON字符串
     * @return JsonObject对象
     */
    public static JsonObject parseJsonObject(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new JsonObject();
        }
        try {
            JsonElement element = JsonParser.parseString(json);
            if (element.isJsonObject()) {
                return element.getAsJsonObject();
            }
            return new JsonObject();
        } catch (Exception e) {
            throw new RuntimeException("JSON对象解析失败: " + json, e);
        }
    }

    /**
     * 解析为JsonArray
     * 对应Fastjson: JSON.parseArray(json) 或 new JSONArray()
     *
     * @param json JSON字符串
     * @return JsonArray对象
     */
    public static JsonArray parseJsonArray(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new JsonArray();
        }
        try {
            JsonElement element = JsonParser.parseString(json);
            if (element.isJsonArray()) {
                return element.getAsJsonArray();
            }
            return new JsonArray();
        } catch (Exception e) {
            throw new RuntimeException("JSON数组解析失败: " + json, e);
        }
    }

    /**
     * JsonObject转Java对象
     * 对应Fastjson: jsonObject.toJavaObject(Class)
     *
     * @param jsonObject JsonObject对象
     * @param clazz      目标类型
     * @param <T>        泛型类型
     * @return 转换后的对象
     */
    public static <T> T toJavaObject(JsonObject jsonObject, Class<T> clazz) {
        if (jsonObject == null) {
            return null;
        }
        return SAFE_GSON.fromJson(jsonObject, clazz);
    }

    /**
     * JsonArray转Java List
     * 对应Fastjson: jsonArray.toJavaList(Class)
     *
     * @param jsonArray JsonArray对象
     * @param clazz     List元素类型
     * @param <T>       泛型类型
     * @return 转换后的List
     */
    public static <T> List<T> toJavaList(JsonArray jsonArray, Class<T> clazz) {
        if (jsonArray == null) {
            return null;
        }
        Type type = new TypeToken<List<T>>() {
        }.getType();
        return SAFE_GSON.fromJson(jsonArray, type);
    }

    /**
     * 创建空的JsonObject
     * 对应Fastjson: new JSONObject()
     *
     * @return 空的JsonObject
     */
    public static JsonObject createJsonObject() {
        return new JsonObject();
    }

    /**
     * 创建空的JsonArray
     * 对应Fastjson: new JSONArray()
     *
     * @return 空的JsonArray
     */
    public static JsonArray createJsonArray() {
        return new JsonArray();
    }

    /**
     * 获取TypeToken，用于复杂泛型类型转换
     * 示例: Type type = GsonUtils.getTypeToken(new TypeToken<Map<String, List<User>>>(){});
     *
     * @param typeToken TypeToken实例
     * @param <T>       泛型类型
     * @return Type对象
     */
    public static <T> Type getTypeToken(TypeToken<T> typeToken) {
        return typeToken.getType();
    }

    /**
     * 判断字符串是否为有效的JSON
     *
     * @param json JSON字符串
     * @return 是否为有效JSON
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        try {
            JsonParser.parseString(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取默认Gson实例（用于特殊场景）
     *
     * @return Gson实例
     */
    public static Gson getGson() {
        return SAFE_GSON;
    }

    /**
     * 获取自定义配置的Gson实例
     *
     * @return GsonBuilder实例，可继续配置
     */
    public static GsonBuilder getGsonBuilder() {
        return new GsonBuilder();
    }
}

