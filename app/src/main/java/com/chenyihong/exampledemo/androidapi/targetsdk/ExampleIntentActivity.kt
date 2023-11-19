package com.chenyihong.exampledemo.androidapi.targetsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chenyihong.exampledemo.databinding.LayoutExampleIntentActivityBinding

class ExampleIntentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutExampleIntentActivityBinding.inflate(layoutInflater).run {
            setContentView(root)
        }
    }
}