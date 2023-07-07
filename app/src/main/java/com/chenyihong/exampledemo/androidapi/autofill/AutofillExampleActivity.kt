package com.chenyihong.exampledemo.androidapi.autofill

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.autofill.AutofillManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutAutofillExampleActivityBinding

const val TAG = "AutofillExampleTag"

class AutoFillExampleActivity : BaseGestureDetectorActivity<LayoutAutofillExampleActivityBinding>() {

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "result ok")
        }
    }

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutAutofillExampleActivityBinding {
        return LayoutAutofillExampleActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val autofillManager = getSystemService(AutofillManager::class.java)
            binding.etAccount.addTextChangedListener {
                it?.run {
                    binding.etPassword.text?.let { passwordText ->
                        binding.btnCommit.isEnabled = isNotEmpty() && passwordText.isNotEmpty()
                    }
                }
            }
            binding.etPassword.addTextChangedListener {
                it?.run {
                    binding.etAccount.text?.let { accountText ->
                        binding.btnCommit.isEnabled = isNotEmpty() && accountText.isNotEmpty()
                    }
                }
            }
            binding.btnCommit.setOnClickListener {
                binding.etAccount.clearFocus()
                binding.etPassword.clearFocus()
                autofillManager.commit()
            }
            binding.btnChangeAutofillService.setOnClickListener {
                // isAutofillSupported判断设备是否支持自动填充服务
                // hasEnabledAutofillServices判断当前使用的自动填充服务是否是我们自定义的
                if (autofillManager.isAutofillSupported && !autofillManager.hasEnabledAutofillServices()) {
                    launcher.launch(Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE).apply {
                        data = Uri.parse("package:com.chenyihong.exampledemo")
                    })
                }
            }
        }
    }
}