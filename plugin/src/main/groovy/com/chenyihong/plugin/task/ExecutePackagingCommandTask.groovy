package com.chenyihong.plugin.task

import com.chenyihong.plugin.extension.ProcessingFlavorExtension
import com.chenyihong.plugin.utils.CommonUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.org.eclipse.jgit.annotations.NonNull

class ExecutePackagingCommandTask extends DefaultTask {

    // 执行的打包命令（打apk或打aab）
    private def command = ProcessingFlavorExtension.COMMAND_ASSEMBLE

    // 打包类型（debug包或release包）
    private def buildType = ProcessingFlavorExtension.BUILD_TYPE_DEBUG

    // 待打包的渠道名（为空时打包所有渠道）
    private def packingFlavors = new ArrayList<String>()

    // 所有渠道名
    private def allFlavors = new ArrayList<String>()

    void setParams(@NonNull String command, @NonNull String buildType, @NonNull List<String> packingFlavors, @NonNull List<String> allFlavors) {
        if (!CommonUtils.isEmpty(command)) {
            this.command = command
        }
        if (!CommonUtils.isEmpty(buildType)) {
            this.buildType = buildType
        }
        if (!packingFlavors.isEmpty()) {
            this.packingFlavors.clear()
            this.packingFlavors.addAll(packingFlavors)
        }
        if (!allFlavors.isEmpty()) {
            this.allFlavors.clear()
            this.allFlavors.addAll(allFlavors)
        }
    }

    @TaskAction
    void doTaskAction() {
        if (packingFlavors.isEmpty() && allFlavors.isEmpty()) {
            CommonUtils.log("flavor info is empty!")
            return
        }

        if (!packingFlavors.isEmpty()) {
            for (String flavorName in packingFlavors) {
                executePackagingCommand(project, "$command${flavorName.capitalize()}$buildType")
            }
        }
    }

    private static void executePackagingCommand(Project project, String command) {
        CommonUtils.log("excute packaging command:$command")
        project.exec {
            commandLine("gradle", "clean")
        }
        project.exec {
            commandLine("gradle", command)
        }
    }
}