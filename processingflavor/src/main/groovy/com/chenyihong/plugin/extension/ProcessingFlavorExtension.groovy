package com.chenyihong.plugin.extension

class ProcessingFlavorExtension {

    /**
     * 生成apk指令
     */
    final String COMMAND_ASSEMBLE = "assemble"

    /**
     * 生成aab指令
     */
    final String COMMAND_BUNDLE = "bundle"

    /**
     * 生成的apk或aab为debug包
     */
    final String BUILD_TYPE_DEBUG = "Debug"

    /**
     * 生成的apk或aab为release包
     */
    final String BUILD_TYPE_RELEASE = "release"

    /**
     * 执行的打包命令（打apk或打aab）
     */
    String executeCommand = COMMAND_ASSEMBLE

    /**
     * 打包类型(debug包或release包)
     */

    String buildType = BUILD_TYPE_DEBUG

    /**
     * 待打包的渠道名（为空时打包所有渠道）
     */
    ArrayList<String> packagingFlavor = []
}