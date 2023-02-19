package com.chenyihong.exampledemo.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.adapter.TestFunctionGroupAdapter
import com.chenyihong.exampledemo.androidapi.animation.AnimatorSetExampleActivity
import com.chenyihong.exampledemo.androidapi.autohide.AutoEdgeHideActivity
import com.chenyihong.exampledemo.androidapi.backpress.BackPressApiActivity
import com.chenyihong.exampledemo.androidapi.biometrics.BiometricActivity
import com.chenyihong.exampledemo.androidapi.camerax.CameraActivity
import com.chenyihong.exampledemo.androidapi.downloadablefont.DownloadableFontActivity
import com.chenyihong.exampledemo.androidapi.fragmentresultapi.FragmentResultApiActivity
import com.chenyihong.exampledemo.androidapi.fullscreen.FullScreenExampleActivity
import com.chenyihong.exampledemo.androidapi.gaid.GaIdActivity
import com.chenyihong.exampledemo.androidapi.gesturedetector.GestureDetectorAActivity
import com.chenyihong.exampledemo.androidapi.gps.GpsSignalActivity
import com.chenyihong.exampledemo.androidapi.motionlayout.MotionLayoutExampleActivity
import com.chenyihong.exampledemo.androidapi.resultapi.ResultApiActivity
import com.chenyihong.exampledemo.androidapi.search.SearchExampleActivity
import com.chenyihong.exampledemo.androidapi.setting.SettingActivity
import com.chenyihong.exampledemo.androidapi.sharesheet.SystemShareActivity
import com.chenyihong.exampledemo.androidapi.shortcuts.ShortcutsActivity
import com.chenyihong.exampledemo.androidapi.toolbar.ToolbarActivity
import com.chenyihong.exampledemo.androidapi.trafficstats.TrafficStatsActivity
import com.chenyihong.exampledemo.customview.CustomChartViewActivity
import com.chenyihong.exampledemo.customview.CustomShadowViewActivity
import com.chenyihong.exampledemo.databinding.LayoutHomeActivityBinding
import com.chenyihong.exampledemo.entity.OptionsChildEntity
import com.chenyihong.exampledemo.flavor.FlavorExampleActivity
import com.chenyihong.exampledemo.tripartite.admob.AdmobExampleActivity
import com.chenyihong.exampledemo.tripartite.login.TripartiteLoginActivity
import com.chenyihong.exampledemo.tripartite.share.TripartiteShareActivity
import com.chenyihong.exampledemo.web.PARAMS_LINK_URL
import com.chenyihong.exampledemo.web.WebViewActivity
import com.chenyihong.exampledemo.web.customtab.CustomTabExampleActivity
import com.minigame.testapp.ui.entity.OptionsEntity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class HomeActivity : AppCompatActivity() {

    private val requestPermissionCode = this.hashCode()

    private val intentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // 这里可以再判断下权限，但是最好不要再次请求，避免用户厌烦
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<LayoutHomeActivityBinding>(this, R.layout.layout_home_activity)

        binding.includeTitle.tvTitle.text = getString(R.string.app_name)

        if (isPostNotificationPermissionNotGranted()) {
            // 申请POST_NOTIFICATIONS权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), requestPermissionCode)
        }

        val functionGroupList = ArrayList<OptionsEntity>()
        functionGroupList.add(OptionsEntity("Android Api", expanded = true, containerTest = arrayListOf(
            OptionsChildEntity("Full Screen Api") { startActivity(Intent(this, FullScreenExampleActivity::class.java)) },
            OptionsChildEntity("Activity Result Api") { startActivity(Intent(this, ResultApiActivity::class.java)) },
            OptionsChildEntity("Fragment Result Api") { startActivity(Intent(this, FragmentResultApiActivity::class.java)) },
            OptionsChildEntity("Back Press Api") { startActivity(Intent(this, BackPressApiActivity::class.java)) },
            OptionsChildEntity("Gesture Detector Api") { startActivity(Intent(this, GestureDetectorAActivity::class.java)) },
            OptionsChildEntity("DownloadableFont") { startActivity(Intent(this, DownloadableFontActivity::class.java)) },
            OptionsChildEntity("Preference Api") { startActivity(Intent(this, SettingActivity::class.java)) },
            OptionsChildEntity("Camera Api") { startActivity(Intent(this, CameraActivity::class.java)) },
            OptionsChildEntity("Biometric Api") { startActivity(Intent(this, BiometricActivity::class.java)) },
            OptionsChildEntity("GpsSignal Api") { startActivity(Intent(this, GpsSignalActivity::class.java)) },
            OptionsChildEntity("Toolbar Api") { startActivity(Intent(this, ToolbarActivity::class.java)) },
            OptionsChildEntity("Search Api") { startActivity(Intent(this, SearchExampleActivity::class.java)) },
            OptionsChildEntity("ShareSheet Api") { startActivity(Intent(this, SystemShareActivity::class.java)) },
            OptionsChildEntity("Shortcuts Api") { startActivity(Intent(this, ShortcutsActivity::class.java)) },
            OptionsChildEntity("TrafficStats Api") { startActivity(Intent(this, TrafficStatsActivity::class.java)) },
            OptionsChildEntity("GAID Api") { startActivity(Intent(this, GaIdActivity::class.java)) },
            OptionsChildEntity("Auto Edge Hide") { startActivity(Intent(this, AutoEdgeHideActivity::class.java)) },
            OptionsChildEntity("Thumb up Animation") { startActivity(Intent(this, AnimatorSetExampleActivity::class.java)) },
            OptionsChildEntity("Motion Api") { startActivity(Intent(this, MotionLayoutExampleActivity::class.java)) }
        )))
        functionGroupList.add(OptionsEntity("Custom View", containerTest = arrayListOf(
            OptionsChildEntity("Custom Chart View") { startActivity(Intent(this, CustomChartViewActivity::class.java)) },
            OptionsChildEntity("Custom Shadow View") { startActivity(Intent(this, CustomShadowViewActivity::class.java)) }
        )))
        functionGroupList.add(OptionsEntity("WebView", containerTest = arrayListOf(
            OptionsChildEntity("Test Js interaction") {
                startActivity(Intent(this, WebViewActivity::class.java).apply { putExtra(PARAMS_LINK_URL, "file:///android_asset/index.html") })
            },
            OptionsChildEntity("Test intercept request") { startActivity(Intent(this, WebViewActivity::class.java).apply { putExtra(PARAMS_LINK_URL, "file:///android_asset/index_intercept_request.html") }) },
            OptionsChildEntity("Test open new window") { startActivity(Intent(this, WebViewActivity::class.java).apply { putExtra(PARAMS_LINK_URL, "file:///android_asset/index_open_tab.html") }) },
            OptionsChildEntity("Google Custom Tab") { startActivity(Intent(this, CustomTabExampleActivity::class.java)) })
        ))
        functionGroupList.add(OptionsEntity("Tripartite sdk", containerTest = arrayListOf(
            OptionsChildEntity("Tripartite Login") { startActivity(Intent(this, TripartiteLoginActivity::class.java)) },
            OptionsChildEntity("Tripartite Share") { startActivity(Intent(this, TripartiteShareActivity::class.java)) },
            OptionsChildEntity("Admob Advertise") { startActivity(Intent(this, AdmobExampleActivity::class.java)) }
        )))
        functionGroupList.add(OptionsEntity("Product Flavor", containerTest = arrayListOf(
            OptionsChildEntity("Flavor Example") { startActivity(Intent(this, FlavorExampleActivity::class.java)) }
        )))

        val testFunctionGroupAdapter = TestFunctionGroupAdapter()
        binding.rvOption.adapter = testFunctionGroupAdapter
        testFunctionGroupAdapter.setNewData(functionGroupList)

        checkKeyStoreHash()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestPermissionCode) {
            if (isPostNotificationPermissionNotGranted()) {
                // 再次判断是否获取到相应的权限了
                // 没有的话告知用户为何我们需要申请权限
                showPermissionStatementDialog()
            }
        }
    }

    private fun isPostNotificationPermissionNotGranted(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    }

    private fun showPermissionStatementDialog() {
        val permissionTipsDialog = AlertDialog.Builder(this)
            .setTitle("Statement of Notification Permission")
            .setMessage("Receive game strategies and information，challenge to become a game winner!")
            .setCancelable(true)
            .setPositiveButton("grant") { dialog, _ ->
                intentLauncher.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = Uri.parse("package:$packageName") })
                dialog.dismiss()
            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        permissionTipsDialog.show()
    }

    private fun checkKeyStoreHash() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                val signingInfo = info.signingInfo
                val apkContentsSigners = signingInfo.apkContentsSigners
                for (signature in apkContentsSigners) {
                    val md: MessageDigest = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val keyStoreHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                    Log.i("keyHash", "KeyHash keyStoreHash:${keyStoreHash}")
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("keyHash", "KeyHash error1:${e.message}")
        } catch (e: NoSuchAlgorithmException) {
            Log.e("keyHash", "KeyHash error2:${e.message}")
        }
    }
}