package com.chenyihong.exampledemo.androidapi.autofill

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.autofill.AutofillManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutAutofillExampleActivityBinding

const val TAG = "AutofillExampleTag"

class AutoFillExampleActivity : BaseGestureDetectorActivity() {

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "result ok")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<LayoutAutofillExampleActivityBinding>(this, R.layout.layout_autofill_example_activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val autofillManager = getSystemService(AutofillManager::class.java)
            val autofillEnable = autofillManager.isEnabled
            val autofillSupported = autofillManager.isAutofillSupported
            val hasEnabledAutofillServices = autofillManager.hasEnabledAutofillServices()
            Log.i(TAG, "autoFillEnable:$autofillEnable, autofillSupported:$autofillSupported, hasEnabledAutofillServices:$hasEnabledAutofillServices")
            /*// 唤起切换填充框架页面
            if (autofillEnable && autofillSupported && !hasEnabledAutofillServices) {
                launcher.launch(Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE).apply {
                    data = Uri.parse("package:com.chenyihong.exampledemo")
                })
            }*/
            /*// 强制使用自动填充
            if (autofillEnable) {
                autofillManager.requestAutofill(binding.etAccount)
                autofillManager.requestAutofill(binding.etPassword)
            }*/
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
        }
    }
}