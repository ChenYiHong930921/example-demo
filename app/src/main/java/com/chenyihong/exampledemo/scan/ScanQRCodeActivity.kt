package com.chenyihong.exampledemo.scan

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.chenyihong.exampledemo.scan.server.TEST_LOG
import com.google.zxing.Result
import com.king.camera.scan.AnalyzeResult
import com.king.camera.scan.CameraScan
import com.king.camera.scan.analyze.Analyzer
import com.king.zxing.BarcodeCameraScanActivity
import com.king.zxing.DecodeConfig
import com.king.zxing.DecodeFormatManager
import com.king.zxing.analyze.QRCodeAnalyzer

class ScanQRCodeActivity : BarcodeCameraScanActivity() {

    override fun initCameraScan(cameraScan: CameraScan<Result>) {
        super.initCameraScan(cameraScan)
        // 播放扫码音效
        cameraScan.setPlayBeep(true)
    }

    override fun createAnalyzer(): Analyzer<Result> {
        return QRCodeAnalyzer(DecodeConfig().apply {
            // 设置仅识别二维码
            setHints(DecodeFormatManager.QR_CODE_HINTS)
        })
    }

    override fun onScanResultCallback(result: AnalyzeResult<Result>) {
        Log.i(TEST_LOG, "onScanResultCallback result:$result")
        // 已获取结果，停止识别二维码
        cameraScan.setAnalyzeImage(false)
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(CameraScan.SCAN_RESULT, result.result.text)
        })
        finish()
    }
}