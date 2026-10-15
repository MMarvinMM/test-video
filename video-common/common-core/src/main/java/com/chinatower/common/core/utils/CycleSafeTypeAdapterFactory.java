package com.chinatower.common.core.utils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 循环安全TypeAdapter工厂
 */
public class CycleSafeTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        // 获取默认的TypeAdapter
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

        // 创建一个新的适配器，包装默认的适配器
        return new TypeAdapter<T>() {
            // 使用ThreadLocal存储已访问的对象，避免多线程问题
            private final ThreadLocal<Map<Object, Object>> visited =
                    ThreadLocal.withInitial(() -> new IdentityHashMap<>());

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }

                Map<Object, Object> visitedMap = visited.get();

                // 检查是否已经访问过该对象（循环引用）
                if (visitedMap.containsKey(value)) {
                    // 发现循环引用，输出引用标记而不是继续递归
                    out.beginObject();
                    out.name("@ref");
                    out.value(getObjectId(value));
                    out.endObject();
                    return;
                }

                // 标记为已访问
                visitedMap.put(value, value);
                try {
                    // 使用委托适配器进行序列化
                    delegate.write(out, value);
                } finally {
                    // 序列化完成后移除标记
                    visitedMap.remove(value);
                    // 如果map为空，清理ThreadLocal，避免内存泄漏
                    if (visitedMap.isEmpty()) {
                        visited.remove();
                    }
                }
            }

            @Override
            public T read(JsonReader in) throws IOException {
                return delegate.read(in);
            }

            private String getObjectId(Object obj) {
                return obj.getClass().getSimpleName() + "@" +
                        Integer.toHexString(System.identityHashCode(obj));
            }
        };
    }
}