package com.chenyihong.exampledemo.androidapi.sharesheet

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import com.chenyihong.exampledemo.databinding.LayoutSystemShareActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.androidapi.resultapi.custom.MultipleLauncherOptions
import com.chenyihong.exampledemo.androidapi.resultapi.custom.PickMultipleMediumContract
import com.chenyihong.exampledemo.androidapi.resultapi.custom.PickSingleMediumContract
import com.chenyihong.exampledemo.base.MimeType

const val TAG = "SystemShare"

class SystemShareActivity : BaseGestureDetectorActivity<LayoutSystemShareActivityBinding>() {

    private val forActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        Log.i(TAG, "launcher callback value : resultCode:${activityResult.resultCode} data${activityResult.data}")
    }
    private val singleMediumPicker = registerForActivityResult(PickSingleMediumContract()) { uri ->
        if (uri != null) {
            val pictureShareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = getMimeType(uri)
            }
            val shareIntent = Intent.createChooser(pictureShareIntent, "ShareSingleMedium")
            forActivityResultLauncher.launch(shareIntent)
        }
    }
    private val multipleMediumPicker = registerForActivityResult(PickMultipleMediumContract()) { uriList ->
        if (uriList.isNotEmpty()) {
            val mediumUris = ArrayList<Uri>(uriList)
            var mimeType = ""
            for (uri in mediumUris) {
                if (mimeType.isEmpty()) {
                    mimeType = handleMultiplePickMimeType(getMimeType(uri))
                } else {
                    if (mimeType != handleMultiplePickMimeType(getMimeType(uri))) {
                        mimeType = MimeType.ALL
                        break
                    }
                }
            }
            val mediumShareIntent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, mediumUris)
                type = mimeType
            }
            val shareIntent = Intent.createChooser(mediumShareIntent, "ShareMultipleMedium")
            forActivityResultLauncher.launch(shareIntent)
        }
    }

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutSystemShareActivityBinding {
        return LayoutSystemShareActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.includeTitle.tvTitle.text = "ShareSheet Api"
        binding.btnShareText.setOnClickListener {
            val onlyTextShareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "this is test share text")
                type = MimeType.TEXT_PLAIN
            }

            val shareIntent = Intent.createChooser(onlyTextShareIntent, "ShareTextTitle")
            forActivityResultLauncher.launch(shareIntent)
        }
        binding.btnShareUrl.setOnClickListener {
            val urlShareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "https://juejin.cn/")
                putExtra(Intent.EXTRA_TITLE, "ShareUrlTitle")
                type = MimeType.TEXT_HTML
            }

            val shareIntent = Intent.createChooser(urlShareIntent, null)
            forActivityResultLauncher.launch(shareIntent)
        }

        binding.btnSharePicture.setOnClickListener {
            singleMediumPicker.launch(null)
        }
        binding.btnSharePictures.setOnClickListener {
            multipleMediumPicker.launch(MultipleLauncherOptions(null, 5))
        }
        binding.btnShareVideo.setOnClickListener {
            singleMediumPicker.launch(MimeType.VIDEO_All)
        }
        binding.btnShareVideos.setOnClickListener {
            multipleMediumPicker.launch(MultipleLauncherOptions(MimeType.VIDEO_All, 5))
        }
        binding.btnShareMultipleMedium.setOnClickListener {
            multipleMediumPicker.launch(MultipleLauncherOptions(MimeType.ALL, 5))
        }
    }

    private fun getMimeType(uri: Uri): String {
        return contentResolver.getType(uri) ?: ""
    }

    private fun handleMultiplePickMimeType(uriMimeType: String): String {
        return when {
            uriMimeType.startsWith(MimeType.IMAGE_HEAD) -> MimeType.IMAGE_ALL
            uriMimeType.startsWith(MimeType.VIDEO_HEAD) -> MimeType.VIDEO_All
            else -> uriMimeType
        }
    }
}