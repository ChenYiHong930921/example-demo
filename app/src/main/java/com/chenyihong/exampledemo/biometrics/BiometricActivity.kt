package com.chenyihong.exampledemo.biometrics

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutBiometricActivityBinding
import java.nio.charset.Charset
import java.util.*

const val TAG = "BiometricApi"

class BiometricActivity : AppCompatActivity() {

    private val forActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        Log.i(TAG, "launcher callback value : resultCode:${activityResult.resultCode} data${activityResult.data}")
        checkBiometricAuthenticate()
    }

    private lateinit var binding: LayoutBiometricActivityBinding

    private var biometricManager: BiometricManager? = null
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null

    private val keyName = "ExampleDemoKey"
    private var encrypt: Boolean = false
    private var encryptedInfo: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_biometric_activity)
        checkBiometricAuthenticate()

        binding.btnBiometric.setOnClickListener {
            biometricPrompt?.run { promptInfo?.let { authenticate(it) } }
        }
        binding.btnEncrypt.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                biometricPrompt?.run {
                    promptInfo?.let {
                        encrypt = true
                        authenticate(it, BiometricPrompt.CryptoObject(CryptographyManager.getEncryptCipher(keyName)))
                    }
                }
            }
        }
        binding.btnDecrypt.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                biometricPrompt?.run {
                    promptInfo?.let {
                        encrypt = false
                        CryptographyManager.getDecryptCipher(keyName)?.let { cipher -> authenticate(it, BiometricPrompt.CryptoObject(cipher)) }
                    }
                }
            }
        }
    }

    private fun checkBiometricAuthenticate() {
        if (biometricManager == null) {
            biometricManager = BiometricManager.from(this)
        }
        biometricManager?.run {
            //如果可以允许用户不使用生物识别而是密码，可以设置DEVICE_CREDENTIAL
            //注意如果DEVICE_CREDENTIAL没有配置，则生成BiometricPrompt.PromptInfo时NegativeButtonText必须配置
            val allowedAuthenticators = BIOMETRIC_STRONG
            when (canAuthenticate(allowedAuthenticators)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    Log.i(TAG, "App can authenticate using biometrics.")
                    initBiometric(allowedAuthenticators)
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Log.e(TAG, "No biometric features available on this device.")
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Log.e(TAG, "Biometric features are currently unavailable.")
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    // Prompts the user to create credentials that your app accepts.
                    Log.e(TAG, "Prompts the user to create credentials that your app accepts.")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Log.e(TAG, "Prompts the user to create credentials that your app accepts 1.")
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, allowedAuthenticators)
                        }
                        forActivityResultLauncher.launch(enrollIntent)
                    } else {
                        Log.e(TAG, "Prompts the user to create credentials that your app accepts 2.")
                        //创建弹窗提示用户录入生物信息（指纹或者人脸），进入设置页面
                        AlertDialog.Builder(this@BiometricActivity)
                            .setTitle("Create credentials")
                            .setMessage("Record fingerprints to log into the ExampleDemo")
                            .setPositiveButton("ok") { _, _ -> gotoSettings() }
                            .setNegativeButton("not now", null)
                            .show()
                    }
                }
                else -> {}
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun initBiometric(allowedAuthenticators: Int) {
        biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this), object : BiometricPrompt.AuthenticationCallback() {
            @SuppressLint("SetTextI18n")
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.i(TAG, "Authentication succeed authenticationType:${result.authenticationType}")
                result.cryptoObject?.cipher?.run {
                    binding.etInputData.text?.let {
                        if (encrypt) {
                            val encryptByteArray = doFinal(it.toString().toByteArray(Charset.defaultCharset()))
                            binding.tvEncryptData.run { post { text = "Encrypt Data : ${Arrays.toString(encryptByteArray)} " } }
                            encryptedInfo = encryptByteArray
                        } else {
                            binding.tvDecryptData.run { post { text = "Decrypt Data : ${String(doFinal(encryptedInfo))}" } }
                        }
                    }
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.e(TAG, "Authentication failed")
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.e(TAG, "Authentication error errorCode:$errorCode, errorMessage:$errString")
            }
        })
        promptInfo = BiometricPrompt.PromptInfo.Builder().run {
            setTitle("Biometric login for ExampleDemo")
            setSubtitle("Login using your biometric credential")
            setAllowedAuthenticators(allowedAuthenticators)
            if (allowedAuthenticators and DEVICE_CREDENTIAL == 0) {
                setNegativeButtonText("use other login")
            }
            build()
        }
    }

    private fun gotoSettings() {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            component = ComponentName("com.android.settings", "com.android.settings.Settings")
        }
        forActivityResultLauncher.launch(intent)
    }
}