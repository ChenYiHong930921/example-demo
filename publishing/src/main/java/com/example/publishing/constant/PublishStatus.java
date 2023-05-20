package com.example.publishing.constant;

public class PublishStatus {

    /**
     * 草稿
     */
    public static final String DRAFT = "draft";

    /**
     * 按阶段发布，同时需要设置发布比例（userFraction）
     */
    public static final String IN_PROGRESS = "inProgress";

    /**
     * 停止按阶段发布
     */
    public static final String HALTED = "halted";

    /**
     * 直接发布
     */
    public static final String COMPLETE = "completed";
}
