package com.chenyihong.plugin.contants;

public enum DefaultValue {

    /**
     * 生成apk指令
     */
    COMMAND_ASSEMBLE("assemble"),

    /**
     * 生成aab指令
     */
    COMMAND_BUNDLE("bundle"),

    /**
     * 生成的apk或aab为debug包
     */
    BUILD_TYPE_DEBUG("Debug"),

    /**
     * 生成的apk或aab为release包
     */
    BUILD_TYPE_RELEASE("release");

    private final String defaultValue;

    DefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return defaultValue;
    }
}
