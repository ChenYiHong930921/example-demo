package com.chenyihong.plugin.extension

class ProcessingFlavorExtension {

    static final String COMMAND_ASSEMBLE = "assemble"

    static final String COMMAND_BUNDLE = "bundle"

    static final String BUILD_TYPE_DEBUG = "debug".capitalize()

    static final String BUILD_TYPE_RELEASE = "release".capitalize()

    /**
     * 执行的打包命令（打apk或打aab）
     */
    String executeCommand = COMMAND_ASSEMBLE

    /**
     * 打包类型(debug包或release包)
     */

    static buildType = BUILD_TYPE_DEBUG

    /**
     * 待打包的渠道名（为空时打包所有渠道）
     */
    ArrayList<String> packagingFlavor = []
}