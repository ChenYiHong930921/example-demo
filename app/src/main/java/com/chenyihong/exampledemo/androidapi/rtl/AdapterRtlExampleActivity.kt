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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutAdapterRtlExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.tvNumberExample.text = "$exampleInt"
        binding.tvNumberFormatExample.text = String.format("%d", exampleInt)

        binding.tvMultiLanguage.text = getString(R.string.adapter_rlt_test, exampleText)
        binding.tvMultiLanguageFormat.text = getString(R.string.adapter_rlt_test, BidiFormatter.getInstance().unicodeWrap(exampleText))
    }
}