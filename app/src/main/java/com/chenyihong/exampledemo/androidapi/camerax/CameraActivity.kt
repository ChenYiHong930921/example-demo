package com.chenyihong.exampledemo.androidapi.camerax

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*

import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.chenyihong.exampledemo.databinding.LayoutCameraActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

import java.io.File

class CameraActivity : BaseGestureDetectorActivity<LayoutCameraActivityBinding>() {

    private lateinit var cameraLifecycle: CameraLifecycle
    private lateinit var imageCapture: ImageCapture

    private var lastStreamState: PreviewView.StreamState? = null

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            bindCamera()
        }
    }

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutCameraActivityBinding {
        return LayoutCameraActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraLifecycle = CameraLifecycle()
        cameraLifecycle.cameraOnCreate()

        requestPermission.launch(Manifest.permission.CAMERA)

        binding.pvCameraPreview.previewStreamState.observe(this) { streamState ->
            Log.i("CameraTest", "previewStreamState :${streamState.name} ,lastStreamState:$lastStreamState")
            if (lastStreamState == null) {
                lastStreamState = streamState
            } else {
                when (streamState) {
                    //输出画面
                    PreviewView.StreamState.STREAMING -> {
                        if (lastStreamState != streamState) {
                            lastStreamState = streamState

                            //开启相机开始输出画面后清空前景
                            binding.pvCameraPreview.foreground = null
                        }
                    }
                    //停止输出画面
                    PreviewView.StreamState.IDLE -> {
                        if (lastStreamState != streamState) {
                            lastStreamState = streamState

                            //关闭相机停止输出画面后仍会停留在最后一帧，设置黑色前景遮挡最后一帧画面
                            binding.pvCameraPreview.foreground = ContextCompat.getDrawable(this, android.R.color.background_dark)
                        }
                    }

                    else -> {}
                }
            }
        }
        binding.btnOpenCamera.setOnClickListener {
            resumeCamera()
        }
        binding.btnCloseCamera.setOnClickListener {
            pauseCamera()
        }
        binding.btnTakePhoto.setOnClickListener {
            val photoFile = getPhotoFile()
            photoFile?.let {
                val outputFileOptions = ImageCapture.OutputFileOptions.Builder(it).build()
                imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i("CameraTest", "take picture succeed savedUri:${outputFileResults.savedUri}")
                        val savedUri = outputFileResults.savedUri
                        binding.ivPhoto.setImageURI(savedUri)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraTest", "take picture failed error:${exception.message}")
                    }
                })
            }
        }
    }

    private fun bindCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            //创建预览
            val preview = Preview.Builder()
                .build()
            //绑定预览View
            preview.setSurfaceProvider(binding.pvCameraPreview.surfaceProvider)

            //选择前置或者后置摄像头
            // CameraSelector.LENS_FACING_FRONT --前置
            // CameraSelector.LENS_FACING_BACK --后置
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            //拍照
            imageCapture = ImageCapture.Builder()
                //设置旋转
                .setTargetRotation(binding.pvCameraPreview.display.rotation)
                //设置拍照模式
                // ImageCaptureCAPTURE_MODE_MINIMIZE_LATENCY --缩短拍摄延迟时间
                // ImageCaptureCAPTURE_MODE_MAXIMIZE_QUALITY --优化照片质量
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(cameraLifecycle, cameraSelector, imageCapture, preview)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun getPhotoFile(): File? {
        val storageFile: File? =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                externalCacheDir
            } else {
                cacheDir
            }

        val photoFile = File.createTempFile("tmp_image_file", ".png", storageFile).apply {
            createNewFile()
            deleteOnExit()
        }

        return photoFile
    }

    private fun resumeCamera() {
        cameraLifecycle.cameraOnStart()
    }

    private fun pauseCamera() {
        cameraLifecycle.cameraOnStop()
    }

    override fun onStart() {
        super.onStart()
        cameraLifecycle.cameraOnStart()
    }

    override fun onResume() {
        super.onResume()
        cameraLifecycle.cameraOnResume()
    }

    override fun onPause() {
        super.onPause()
        cameraLifecycle.cameraOnPause()
    }

    override fun onStop() {
        super.onStop()
        cameraLifecycle.cameraOnStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraLifecycle.cameraOnDestroyed()
    }
}