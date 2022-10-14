package com.chenyihong.exampledemo.home

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.adapter.TestFunctionGroupAdapter
import com.chenyihong.exampledemo.backpress.BackPressApiActivity
import com.chenyihong.exampledemo.biometrics.BiometricActivity
import com.chenyihong.exampledemo.camerax.CameraActivity
import com.chenyihong.exampledemo.customview.CustomChartViewActivity
import com.chenyihong.exampledemo.customview.CustomShadowViewActivity
import com.chenyihong.exampledemo.databinding.LayoutHomeActivityBinding
import com.chenyihong.exampledemo.downloadablefont.DownloadableFontActivity
import com.chenyihong.exampledemo.entity.OptionsChildEntity
import com.chenyihong.exampledemo.fragmentresultapi.FragmentResultApiActivity
import com.chenyihong.exampledemo.fullscreen.FullScreenExampleActivity
import com.chenyihong.exampledemo.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.gesturedetector.GestureDetectorAActivity
import com.chenyihong.exampledemo.gps.GpsSignalActivity
import com.chenyihong.exampledemo.resultapi.ResultApiActivity
import com.chenyihong.exampledemo.search.SearchExampleActivity
import com.chenyihong.exampledemo.setting.SettingActivity
import com.chenyihong.exampledemo.share.TripartiteShareActivity
import com.chenyihong.exampledemo.share.systemshare.SystemShareActivity
import com.chenyihong.exampledemo.toolbar.ToolbarActivity
import com.chenyihong.exampledemo.tripartitelogin.TripartiteLoginActivity
import com.chenyihong.exampledemo.web.PARAMS_LINK_URL
import com.chenyihong.exampledemo.web.WebViewActivity
import com.minigame.testapp.ui.entity.OptionsEntity

class HomeActivity : BaseGestureDetectorActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<LayoutHomeActivityBinding>(this, R.layout.layout_home_activity)

        binding.includeTitle.tvTitle.text = getString(R.string.app_name)

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
            OptionsChildEntity("ShareSheet Api") { startActivity(Intent(this, SystemShareActivity::class.java)) }
        )))
        functionGroupList.add(OptionsEntity("Custom View", containerTest = arrayListOf(
            OptionsChildEntity("Custom Chart View") { startActivity(Intent(this, CustomChartViewActivity::class.java)) },
            OptionsChildEntity("Custom Shadow View") { startActivity(Intent(this, CustomShadowViewActivity::class.java)) }
        )))
        functionGroupList.add(OptionsEntity("WebView", containerTest = arrayListOf(
            OptionsChildEntity("Test Js interaction") { startActivity(Intent(this, WebViewActivity::class.java).apply { putExtra(PARAMS_LINK_URL, "file:///android_asset/index.html") }) },
            OptionsChildEntity("Test intercept request") { startActivity(Intent(this, WebViewActivity::class.java).apply { putExtra(PARAMS_LINK_URL, "file:///android_asset/index_intercept_request.html") }) },
            OptionsChildEntity("Test open new window") { startActivity(Intent(this, WebViewActivity::class.java).apply { putExtra(PARAMS_LINK_URL, "file:///android_asset/index_open_tab.html") }) }
        )))
        functionGroupList.add(OptionsEntity("Tripartite sdk", containerTest = arrayListOf(
            OptionsChildEntity("Tripartite Login") { startActivity(Intent(this, TripartiteLoginActivity::class.java)) },
            OptionsChildEntity("Tripartite Share") { startActivity(Intent(this, TripartiteShareActivity::class.java)) }
        )))

        val testFunctionGroupAdapter = TestFunctionGroupAdapter()
        binding.rvOption.adapter = testFunctionGroupAdapter
        testFunctionGroupAdapter.setNewData(functionGroupList)
    }
}