package com.chinatower.platform.project.system.constants;

public enum RedisSourceType {
    DATA("1"),
    SQL("2");

    private String value;

    RedisSourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
}
