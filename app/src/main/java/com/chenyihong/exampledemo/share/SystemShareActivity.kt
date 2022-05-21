package com.chenyihong.exampledemo.share

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutSystemShareActivityBinding
import com.chenyihong.exampledemo.resultapi.custom.PickSinglePhotoContract

const val TAG = "SystemShare"

class SystemShareActivity : AppCompatActivity() {

    private val pickPhoto = registerForActivityResult(PickSinglePhotoContract()) { uri ->
        if (uri != null) {
            val pictureShareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = MimeType.IMAGE_JPEG
            }
            val shareIntent = Intent.createChooser(pictureShareIntent, "SharePictureTitle")
            startActivity(shareIntent)
        }
    }

    private val pickVideo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val pictureShareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = MimeType.VIDEO_MP4
            }
            val shareIntent = Intent.createChooser(pictureShareIntent, "ShareVideoTitle")
            startActivity(shareIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<LayoutSystemShareActivityBinding>(this, R.layout.layout_system_share_activity)

        binding.btnShareText.setOnClickListener {
            val onlyTextShareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "this is test share text")
                type = MimeType.TEXT_PLAIN
            }

            val shareIntent = Intent.createChooser(onlyTextShareIntent, "ShareTextTitle")
            startActivity(shareIntent)
        }
        binding.btnShareUrl.setOnClickListener {
            val urlShareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "https://juejin.cn/")
                putExtra(Intent.EXTRA_TITLE, "ShareUrlTitle")
                type = MimeType.TEXT_HTML
            }

            val shareIntent = Intent.createChooser(urlShareIntent, null)
            startActivity(shareIntent)
        }

        binding.btnSharePicture.setOnClickListener {
            pickPhoto.launch(null)
        }
        binding.btnShareVideo.setOnClickListener {
            pickVideo.launch(MimeType.VIDEO_All)
        }
    }
}