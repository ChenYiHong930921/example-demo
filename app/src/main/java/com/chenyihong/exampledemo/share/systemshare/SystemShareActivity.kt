package com.chenyihong.exampledemo.share.systemshare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutSystemShareActivityBinding
import com.chenyihong.exampledemo.resultapi.custom.PickMultipleMediumContract
import com.chenyihong.exampledemo.resultapi.custom.PickSingleMediumContract
import com.chenyihong.exampledemo.share.MimeType

const val TAG = "SystemShare"

class SystemShareActivity : AppCompatActivity() {

    private val sharePhotoUri = ArrayList<Uri>()
    private val shareVideoUrl = ArrayList<Uri>()

    val forActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        Log.i(TAG, "lanuncher callback value : resultCode:${activityResult.resultCode} data${activityResult.data}")
    }
    private val pickPhoto = registerForActivityResult(PickSingleMediumContract()) { uri ->
        if (uri != null) {
            val pictureShareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = MimeType.IMAGE_JPEG
            }
            val shareIntent = Intent.createChooser(pictureShareIntent, "SharePictureTitle")
            forActivityResultLauncher.launch(shareIntent)
        }
    }
    private val pickPhotos = registerForActivityResult(PickMultipleMediumContract()) { uriList ->
        if (uriList.isNotEmpty()) {
            sharePhotoUri.addAll(uriList)
            val photoShareIntent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, sharePhotoUri)
                type = MimeType.IMAGE_ALL
            }
            val shareIntent = Intent.createChooser(photoShareIntent, "SharePhotosTitle")
            forActivityResultLauncher.launch(shareIntent)
        }
    }
    private val pickVideo = registerForActivityResult(PickSingleMediumContract(MimeType.VIDEO_All)) { uri ->
        if (uri != null) {
            val videoShareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = MimeType.VIDEO_MP4
            }
            val shareIntent = Intent.createChooser(videoShareIntent, "ShareVideoTitle")
            forActivityResultLauncher.launch(shareIntent)
        }
    }
    private val pickVideos = registerForActivityResult(PickMultipleMediumContract(MimeType.VIDEO_All)) { uri ->
        if (uri.isNotEmpty()) {
            shareVideoUrl.addAll(uri)
            val videoShareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, shareVideoUrl)
                type = MimeType.VIDEO_All
            }
            val shareIntent = Intent.createChooser(videoShareIntent, "ShareVideosTitle")
            forActivityResultLauncher.launch(shareIntent)
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
            pickPhoto.launch(null)
        }
        binding.btnSharePictures.setOnClickListener {
            pickPhotos.launch(null)
        }
        binding.btnShareVideo.setOnClickListener {
            pickVideo.launch(MimeType.VIDEO_All)
        }
        binding.btnShareVideos.setOnClickListener {
            pickVideos.launch(MimeType.VIDEO_All)
        }
        binding.btnShareMultipleMedium.setOnClickListener {
            val mediumUris = ArrayList<Uri>()
            mediumUris.addAll(sharePhotoUri)
            mediumUris.addAll(shareVideoUrl)
            if (mediumUris.isNotEmpty()) {
                val mediumShareIntent = Intent().apply {
                    action = Intent.ACTION_SEND_MULTIPLE
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, mediumUris)
                    type = MimeType.ALL
                }
                val shareIntent = Intent.createChooser(mediumShareIntent, "ShareMultipleMediumTitle")
                forActivityResultLauncher.launch(shareIntent)
            }
        }
    }
}