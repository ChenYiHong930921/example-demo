package com.chenyihong.exampledemo.tripartite.dom4j

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutDom4jExampleActivityBinding
import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.Namespace
import org.dom4j.QName
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import java.io.IOException
import java.io.StringWriter

const val TAG = "dom4jExample"

class Dom4jExampleActivity : BaseGestureDetectorActivity() {

    // Android 命名空间
    private val android = Namespace("android", "http://schemas.android.com/apk/res/android")

    // android:name对应的QName
    private val androidName = QName.get("name", android)

    // android:exported对应的QName
    private val androidExported = QName.get("exported", android)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LayoutDom4jExampleActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_dom4j_example_activity)
        binding.includeTitle.tvTitle.text = "dom4j Example"

        val reader = SAXReader()
        // 读取xml文件，解析为Document对象。
        // read方法传入参数有File、URL、InputStream、Reader、InputSource等。
        val manifestDocument = reader.read(assets.open("AndroidManifest.xml"))
        // 获取Document的根元素，即Manifest
        val manifestElement = manifestDocument.rootElement
        // 获取application元素
        val applicationElement = manifestElement.element("application")

        // 获取application元素下，除了provider外的其他四大组件的所有元素。
        // Element.elements(String name) 可以获取元素中包含的所有同名的子元素。
        // Element.element(String name) 可以获取元素中第一个同名的子元素。
        val componentElement = ArrayList<Element>().apply {
            addAll(applicationElement.elements("activity"))
            addAll(applicationElement.elements("receiver"))
            addAll(applicationElement.elements("service"))
        }

        componentElement.forEach {
            if (hadIntentFilter(it) && !hadExportedAttribute(it)) {
                // 有intent-filter但是没有android:exported属性，需要添加
                // 启动页需要设置为true，其他设置为false
                it.addAttribute(androidExported, mainLauncherActivity(it).toString())
            }
        }

        binding.tvResultValue.text = writeManifestToString(manifestDocument)
    }

    // 判断是否包含intent-filter
    private fun hadIntentFilter(componentElement: Element): Boolean {
        return componentElement.element("intent-filter") != null
    }

    // 判断是否包含android:exported
    private fun hadExportedAttribute(componentElement: Element): Boolean {
        return componentElement.attribute(androidExported) != null
    }

    // 判断Activity是否为启动页，是的话exported属性需要设置为true
    private fun mainLauncherActivity(componentElement: Element): Boolean {
        return if (componentElement.name == "activity") {
            var hadLauncherCategory = false
            var hadMainAction = false
            componentElement.element("intent-filter").elements().forEach {
                when (it.attribute(androidName).value) {
                    "android.intent.category.LAUNCHER" -> hadLauncherCategory = true
                    "android.intent.action.MAIN" -> hadMainAction = true
                }
            }
            hadLauncherCategory && hadMainAction
        } else {
            false
        }
    }

    private fun writeManifestToString(document: Document): String {
        var resultValue = ""
        try {
            val format = OutputFormat.createPrettyPrint()
            // 设置缩进为4
            format.setIndentSize(4)
            val stringWriter = StringWriter()
            val xmlWriter = XMLWriter(stringWriter, format)
            xmlWriter.write(document)
            xmlWriter.flush()
            resultValue = stringWriter.toString()
            xmlWriter.close()
            stringWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return resultValue
    }
}