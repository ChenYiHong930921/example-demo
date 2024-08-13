package com.chenyihong.plugin

import com.chenyihong.plugin.api.OkHttpHelper
import com.chenyihong.plugin.api.RequestCallback
import com.chenyihong.plugin.entity.FlavorParamsEntity
import com.chenyihong.plugin.extension.ProcessingFlavorExtension
import com.chenyihong.plugin.task.ExecutePackagingCommandTask
import com.chenyihong.plugin.utils.CommonUtils
import jxl.Workbook
import okhttp3.ResponseBody
import org.dom4j.Element
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProcessingFlavorPlugin implements Plugin<Project> {

    private def okHttpHelper = new OkHttpHelper()

    /**
     * 生成签名文件时设置的密码
     * 如果要使用不一样的密码，可以考虑放到FlavorParamsEntity中
     */
    private String keystorePassword = "test123456"

    /**
     * 渠道多的时候，签名文件也会对应的增多
     * 放到一个单独的文件中，项目结构更清晰
     */
    private String keystoreSaveFolder = "signKey"

    private String dimensionValue = "apptag"

    private def flavorParams = new ArrayList<FlavorParamsEntity>()

    @Override
    void apply(Project target) {
        def android = target.extensions.findByName("android")
        if (!android || !android.hasProperty("applicationVariants")) {
            throw IllegalArgumentException("must apply this plugin after 'com.android.application'")
        }

        def processingFlavorExtension = target.extensions.create("ProcessingFlavorExtension", ProcessingFlavorExtension)

        okHttpHelper.init()

        // 从渠道参数Excel解析出需要的数据
        def flavorParamsExcel = new File(target.projectDir, "flavor_params.xls")
        parseFlavorParamsFromExcel(flavorParamsExcel)

        if (!flavorParams.isEmpty()) {
            android.flavorDimensionList.add(dimensionValue)

            for (FlavorParamsEntity flavorParam : flavorParams) {
                def flavorName = flavorParam.flavorName

                // 配置签名
                def keystoreFile = generateKeystoreIfNeed(target, flavorParam)
                android.signingConfigs.register(flavorName, {
                    keyAlias keystoreFile.getName()
                    keyPassword keystorePassword
                    storeFile keystoreFile
                    storePassword keystorePassword
                })

                // 配置flavor
                generateFlavorFolderAndResIfNeed(target, flavorParam)
                android.productFlavors.register(flavorName, {
                    applicationId flavorParam.applicationId
                    versionCode flavorParam.versionCode
                    versionName flavorParam.versionName

                    manifestPlaceholders = [app_icon      : "@drawable/${flavorName}",
                                            app_round_icon: "@drawable/${flavorName}"]

                    signingConfig android.signingConfigs.getByName(flavorName)

                    buildConfigField("String", "open_website", "\"${flavorParam.openWebsite}\"")

                    dimension dimensionValue
                })
            }
        }

        target.afterEvaluate {
            def executePackagingCommandTask = target.getTasks().register("ExecutePackagingCommandTask", ExecutePackagingCommandTask).get()
            executePackagingCommandTask.setParams(processingFlavorExtension.executeCommand, processingFlavorExtension.buildType, processingFlavorExtension.packagingFlavor, flavorParams.collect { it.flavorName })
            executePackagingCommandTask.group = "Processing Flavor"
        }
    }

    private void parseFlavorParamsFromExcel(File targetExcel) {
        if (targetExcel.exists()) {
            try {
                def flavorParamsSheet = Workbook.getWorkbook(targetExcel).getSheet(0)
                for (int rowIndex = 1; rowIndex < flavorParamsSheet.getRows(); rowIndex++) {
                    def flavorName = flavorParamsSheet.getCell(0, rowIndex).getContents()
                    def applicationId = flavorParamsSheet.getCell(1, rowIndex).getContents()
                    def versionCode = flavorParamsSheet.getCell(2, rowIndex).getContents()
                    def versionName = flavorParamsSheet.getCell(3, rowIndex).getContents()
                    def appName = flavorParamsSheet.getCell(4, rowIndex).getContents()
                    // 实例中使用网络图标
                    def appIconPath = flavorParamsSheet.getCell(5, rowIndex).getContents()
                    def openWebsite = flavorParamsSheet.getCell(6, rowIndex).getContents()
                    if (CommonUtils.isEmpty(flavorName) || CommonUtils.isEmpty(applicationId) || CommonUtils.isEmpty(versionCode) ||
                            CommonUtils.isEmpty(versionName) || CommonUtils.isEmpty(appName) || CommonUtils.isEmpty(appIconPath) || CommonUtils.isEmpty(openWebsite)) {
                        continue
                    }
                    flavorParams.add(new FlavorParamsEntity(flavorName, applicationId, Integer.parseInt(versionCode), versionName, appName, appIconPath, openWebsite))
                }
            } catch (Exception e) {
                e.printStackTrace()
                CommonUtils.log("parse flavor params from excel error due to " + e.getMessage())
            }
        }
    }

    private File generateKeystoreIfNeed(Project project, FlavorParamsEntity flavorParam) {
        def finalKeystoreSaveFolder = new File(project.projectDir, keystoreSaveFolder)
        if (finalKeystoreSaveFolder.mkdirs()) {
            CommonUtils.log("create keystore save folder succeed")
        }

        // 示例中直接使用渠道名作为签名文件的名字和别名
        // 需要特别定制可以考虑放到FlavorParamsEntity中
        def keystoreFileName = flavorParam.flavorName
        def keystoreFile = new File(finalKeystoreSaveFolder, keystoreFileName)
        if (!keystoreFile.exists()) {
            // 对应用AS生成签名时需要填写的信息
            def dName = "CN=$keystoreFileName, OU=$keystoreFileName, O=$keystoreFileName, L=$keystoreFileName, ST=$keystoreFileName, C=$keystoreFileName"
            // 通过命令行生成签名文件
            project.exec {
                commandLine(
                        "keytool", "-genkey",
                        "-alias", keystoreFileName,
                        "-keypass", keystorePassword,
                        "-keyalg", "RSA",
                        "-validity", "10950",
                        "-keystore", keystoreFile.getAbsolutePath(),
                        "-storepass", keystorePassword,
                        "-dname", dName
                )
            }
        }
        return keystoreFile
    }

    private void generateFlavorFolderAndResIfNeed(Project project, FlavorParamsEntity flavorParam) {
        def srcFolder = new File(project.projectDir, "src")
        // 生成渠道文件夹
        def flavorFolder = new File(srcFolder, flavorParam.flavorName)
        if (flavorFolder.mkdirs()) {
            CommonUtils.log("${flavorParam.flavorName} flavor folder create succeed")
        }
        // 生成渠道的res文件夹
        def flavorResFolder = new File(flavorFolder, "res")
        if (flavorResFolder.mkdirs()) {
            CommonUtils.log("${flavorParam.flavorName} flavor res folder create succeed")
        }
        // 生成渠道的drawable文件夹（放app icon）
        def flavorDrawableFolder = new File(flavorResFolder, "drawable")
        if (flavorDrawableFolder.mkdirs()) {
            CommonUtils.log("${flavorParam.flavorName} flavor res drawable folder create succeed")
        }
        // 生成渠道的values文件夹（放strings.xml，并配置app name）
        def flavorValuesFolder = new File(flavorResFolder, "values")
        if (flavorValuesFolder.mkdirs()) {
            CommonUtils.log("${flavorParam.flavorName} flavor res values folder create succeed")
        }
        downloadAppIconIfNeed(flavorDrawableFolder, flavorParam.appIconPath, flavorParam.flavorName)
        generateStringsXmlIfNeed(flavorValuesFolder, flavorParam.flavorName)
    }

    private void downloadAppIconIfNeed(File flavorDrawableFolder, String downloadUrl, String appIconName) {
        def appIconFileName = "$appIconName.${CommonUtils.getImageType(downloadUrl)}"
        def appIconFile = new File(flavorDrawableFolder, appIconFileName)
        if (!appIconFile.exists()) {
            CommonUtils.log("start download appIconName")
            okHttpHelper.downloadFileFromPath(downloadUrl, new RequestCallback() {
                @Override
                void onResponse(boolean success, ResponseBody responseBody) {
                    if (success && responseBody != null) {
                        // 下载图标到渠道对应的drawable文件夹中，若已经存在，不重新创建
                        CommonUtils.saveFileToTargetFolder(flavorDrawableFolder, appIconFileName, responseBody.bytes(), false)
                    }
                }

                @Override
                void onFailure(String errorMessage) {
                    CommonUtils.log("download app icon failed errorMessage:$errorMessage")
                }
            })
        }
    }

    private void generateStringsXmlIfNeed(File flavorValuesFolder, String appName) {
        def stringsXmlFile = new File(flavorValuesFolder, "strings.xml")
        if (stringsXmlFile.exists()) {
            // 存在则判断当前appName是否跟配置一样，不是则修改
            changeAppNameIfNeed(stringsXmlFile, appName)
        } else {
            // 不存在则重新创建
            def stringsXmlContent = "<resources>\n    <string name=\"app_name\">$appName</string>\n</resources>"
            CommonUtils.writeDataToFile(stringsXmlFile, stringsXmlContent)
        }
    }

    private void changeAppNameIfNeed(File stringsXmlFile, String appName) {
        try {
            def saxReader = new SAXReader()
            def needSaveDocument = false
            def resourceDocument = saxReader.read(stringsXmlFile)
            def resourceElement = resourceDocument.rootElement
            def stringElements = resourceElement.elements("string")
            for (Element element : stringElements) {
                if ("app_name" == element.attributeValue("name")) {
                    if (element.textTrim != appName) {
                        CommonUtils.log("element name:${element.name}, oldValue:${element.textTrim}, newValue:${appName}")
                        element.setText(appName)
                        needSaveDocument = true
                    }
                    break
                }
            }
            if (needSaveDocument) {
                OutputFormat format = OutputFormat.createPrettyPrint()
                format.setIndentSize(4)
                XMLWriter logWriter = new XMLWriter(System.out, format)
                logWriter.write(resourceDocument)
                logWriter.close()
                XMLWriter writer = new XMLWriter(stringsXmlFile, format)
                writer.write(resourceDocument)
                writer.close()
            }
        } catch (Exception e) {
            e.printStackTrace()
            CommonUtils.log("read strings xml file failed due to ${e.message}")
        }
    }
}