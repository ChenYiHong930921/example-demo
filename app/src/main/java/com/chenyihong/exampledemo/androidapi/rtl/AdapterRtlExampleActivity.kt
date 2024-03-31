package com.chenyihong.exampledemo.androidapi.rtl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.BidiFormatter
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutAdapterRtlExampleActivityBinding

class AdapterRtlExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutAdapterRtlExampleActivityBinding

    private val exampleInt = 100102
    private val exampleText = "15 Bay Street, Laurel, CA"
    private val exampleData = arrayOf("测试测试测试测试", "aadaada", "hahaha", "这是一个测试数据", "yyddd", "测试用测试用", "test data", "example", "akdjfj", "yyds")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutAdapterRtlExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.tvNumberExample.text = "$exampleInt"
        binding.tvNumberFormatExample.text = String.format("%d", exampleInt)

        binding.tvMultiLanguage.text = getString(R.string.adapter_rlt_test, exampleText)
        binding.tvMultiLanguageFormat.text = getString(R.string.adapter_rlt_test, BidiFormatter.getInstance().unicodeWrap(exampleText))

        binding.btnAddData.setOnClickListener {
            val data = ArrayList<String>()
            // 从测试数据中随机生成8个元素
            repeat(8) {
                data.add(exampleData.random())
            }
            binding.eflExampleDataContainer.setData(data)
        }
    }
}