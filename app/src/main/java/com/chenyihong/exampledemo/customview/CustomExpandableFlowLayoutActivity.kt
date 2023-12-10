package com.chenyihong.exampledemo.customview

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chenyihong.exampledemo.databinding.LayoutExampleExpandableFlowLayoutActivityBinding
import com.chenyihong.exampledemo.web.TAG
import com.google.android.material.snackbar.Snackbar

class CustomExpandableFlowLayoutActivity : AppCompatActivity() {

    private lateinit var binding: LayoutExampleExpandableFlowLayoutActivityBinding

    private val exampleData = arrayOf("测试测试测试测试", "aadaada", "hahaha", "这是一个测试数据", "yyddd", "测试用测试用", "test data", "example", "akdjfj", "yyds")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutExampleExpandableFlowLayoutActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.eflExampleDataContainer.elementClickCallback = {
            showSnakeBar("click item:$it")
        }
        binding.btnAddData.setOnClickListener {
            val data = ArrayList<String>()
            // 从测试数据中随机生成8个元素
            repeat(8) {
                data.add(exampleData.random())
            }
            binding.eflExampleDataContainer.setData(data)
        }
    }

    private fun showSnakeBar(message: String) {
        Log.i(TAG, message)
        runOnUiThread {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }
}