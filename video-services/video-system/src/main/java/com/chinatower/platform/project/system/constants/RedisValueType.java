package com.chinatower.platform.project.system.constants;

public enum RedisValueType {
    STRING("string"),
    LIST("list"),
    HASH("hash");

    private String value;

    RedisValueType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
}
