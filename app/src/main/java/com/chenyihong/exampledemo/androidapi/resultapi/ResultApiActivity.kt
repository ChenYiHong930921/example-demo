package com.chenyihong.exampledemo.androidapi.resultapi

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.BuildConfig
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.resultapi.custom.PickMultipleMediumContract
import com.chenyihong.exampledemo.androidapi.resultapi.custom.PickSingleMediumContract
import com.chenyihong.exampledemo.databinding.LayoutResultApiActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import java.io.File

const val TAG = "ResultApi"

class ResultApiActivity : BaseGestureDetectorActivity() {

    private lateinit var binding: LayoutResultApiActivityBinding

    //<editor-folder desc = "default permission contract">

    private var requestPermissionName: String = ""
    private var takePhoto = false

    private val requestSinglePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted: Boolean ->
        if (granted) {
            if (Manifest.permission.CAMERA == requestPermissionName) {
                if (takePhoto) {
                    takePicture.launch(photoUri)
                } else {
                    takePicturePreview.launch(null)
                }
            } else {
                //同意授权
                val message = "$requestPermissionName already granted"
                Log.d(TAG, message)
                showToast(message)
            }
        } else {
            Log.d(TAG, "$requestPermissionName not granted")
            //未同意授权
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(requestPermissionName)) {
                    //用户拒绝权限并且系统不再弹出请求权限的弹窗
                    //这时需要我们自己处理，比如自定义弹窗告知用户为何必须要申请这个权限
                    Log.e(TAG, "$requestPermissionName not granted and should not show rationale")
                }
            }
        }
    }

    private val requestMultiplePermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean> ->
        permissions.entries.forEach {
            val permissionName = it.key
            if (it.value) {
                //同意授权
                val message = "$permissionName already granted"
                Log.d(TAG, message)
                showToast(message)
            } else {
                Log.d(TAG, "$permissionName not granted")
                //未同意授权
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!shouldShowRequestPermissionRationale(permissionName)) {
                        //用户拒绝权限并且系统不再弹出请求权限的弹窗
                        //这时需要我们自己处理，比如自定义弹窗告知用户为何必须要申请这个权限
                        Log.d(TAG, "$permissionName not granted and should not show rationale")
                    }
                }
            }
        }
    }

    //</editor-folder>

    //<editor-folder desc = "default photo contract">

    private var photoUri: Uri? = null

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        Log.d(TAG, "take picture success:$success")
        if (success) {
            photoUri?.let {
                binding.ivPhoto.setImageURI(it)
            }
        }
    }

    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { photoPreview ->
        Log.d(TAG, "take picture preview photoPreview:$photoPreview")
        //该合约返回的是Bitmap,如需保存要进行额外的处理
        binding.ivPhoto.setImageBitmap(photoPreview)
    }

    private val selectPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        Log.d(TAG, "select photo uri:$uri")
        binding.ivPhoto.setImageURI(uri)
    }

    private val selectMultiplePhoto = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
        Log.d(TAG, "select multiple photo uriList:$uriList")
        if (uriList.size > 1) {
            binding.ivPhoto.setImageURI(uriList[0])
            binding.ivPhoto1.setImageURI(uriList[1])
        }
    }

    //</editor-folder>

    //<editor-folder desc = "custom contract">

    private val pickSinglePhoto = registerForActivityResult(PickSingleMediumContract()) { uri ->
        Log.d(TAG, "pick photo uri:$uri")
        binding.ivPhoto.setImageURI(uri)
    }

    private val pickMultiplePhoto = registerForActivityResult(PickMultipleMediumContract()) { uriList ->
        Log.d(TAG, "pick multiple photo uriList:$uriList")
        if (uriList.size > 1) {
            binding.ivPhoto.setImageURI(uriList[0])
            binding.ivPhoto1.setImageURI(uriList[1])
        }
    }

    //</editor-folder>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_result_api_activity)

        binding.includeTitle.tvTitle.text = "Activity Result Api"

        photoUri = getPhotoFileUri()

        binding.btnRequestPermission.setOnClickListener {
            requestPermissionName = Manifest.permission.ACCESS_FINE_LOCATION
            requestSinglePermissionLauncher.launch(requestPermissionName)
        }

        binding.btnRequestMultiplePermission.setOnClickListener {
            requestMultiplePermissionsLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE))
        }

        binding.btnTakePhoto.setOnClickListener {
            requestPermissionName = Manifest.permission.CAMERA
            requestSinglePermissionLauncher.launch(requestPermissionName)
            takePhoto = true
        }

        binding.btnTakePhotoPreview.setOnClickListener {
            requestPermissionName = Manifest.permission.CAMERA
            requestSinglePermissionLauncher.launch(requestPermissionName)
            takePhoto = false
        }

        binding.btnSelectPhoto.setOnClickListener {
            //传入的参数就是你想要选择的资源类型
            selectPhoto.launch("image/*")
        }

        binding.btnSelectMultiplePhoto.setOnClickListener {
            //传入的参数就是你想要选择的资源类型
            selectMultiplePhoto.launch("image/*")
        }

        binding.btnSelectPhotoCustomContract.setOnClickListener {
            pickSinglePhoto.launch(null)
        }

        binding.btnSelectMultiplePhotoCustomContract.setOnClickListener {
            pickMultiplePhoto.launch(null)
        }
    }

    //获取保存照片的Uri
    private fun getPhotoFileUri(): Uri? {
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

        return FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", photoFile)
    }
}