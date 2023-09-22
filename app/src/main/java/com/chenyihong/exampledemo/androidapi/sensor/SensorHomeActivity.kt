package com.chenyihong.exampledemo.androidapi.sensor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutSensorHomeActivityBinding

class SensorHomeActivity : BaseGestureDetectorActivity<LayoutSensorHomeActivityBinding>() {
    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutSensorHomeActivityBinding {
        return LayoutSensorHomeActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {
            btnDetectingByOrientationSensor.setOnClickListener {
                startActivity(Intent(this@SensorHomeActivity, SensorExampleActivity::class.java).apply {
                    putExtra("type", TYPE_DETECTING_SCREEN_ORIENTATION_BY_ORIENTATION_SENSOR)
                })
            }
            btnDetectingBySensorManager.setOnClickListener {
                startActivity(Intent(this@SensorHomeActivity, SensorExampleActivity::class.java).apply {
                    putExtra("type", TYPE_DETECTING_SCREEN_ORIENTATION_BY_SENSOR_MANAGER)
                })
            }
            btnDetectingShaking.setOnClickListener {
                startActivity(Intent(this@SensorHomeActivity, SensorExampleActivity::class.java).apply {
                    putExtra("type", TYPE_DETECTING_SHAKING)
                })
            }
            btnCountingStepByStepCounter.setOnClickListener {
                startActivity(Intent(this@SensorHomeActivity, SensorExampleActivity::class.java).apply {
                    putExtra("type", TYPE_DETECTING_STEP_BY_STEP_COUNTER)
                })
            }
            btnCountingStepByStepDetector.setOnClickListener {
                startActivity(Intent(this@SensorHomeActivity, SensorExampleActivity::class.java).apply {
                    putExtra("type", TYPE_DETECTING_STEP_BY_STEP_DETECTOR)
                })
            }
            btnDetectingLight.setOnClickListener {
                startActivity(Intent(this@SensorHomeActivity, SensorExampleActivity::class.java).apply {
                    putExtra("type", TYPE_DETECTING_LIGHT)
                })
            }
        }
    }
}