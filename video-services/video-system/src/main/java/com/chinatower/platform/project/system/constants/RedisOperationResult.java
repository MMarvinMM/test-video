package com.chinatower.platform.project.system.constants;

public enum RedisOperationResult {
    SUCCESS("1"),
    FAIL("0");

    private String value;

    RedisOperationResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
