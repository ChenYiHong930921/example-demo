package com.chenyihong.plugin.extension

import com.chenyihong.plugin.contants.DefaultValue

class ProcessingFlavorExtension {

    /**
     * 执行的打包命令（打apk或打aab）
     */
    String executeCommand = DefaultValue.COMMAND_ASSEMBLE

    /**
     * 打包类型(debug包或release包)
     */

    String buildType = DefaultValue.BUILD_TYPE_DEBUG

    /**
     * 待打包的渠道名（为空时打包所有渠道）
     */
    ArrayList<String> packagingFlavor = []
}