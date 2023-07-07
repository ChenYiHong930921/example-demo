package com.chenyihong.exampledemo.tripartite.share

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.chenyihong.exampledemo.databinding.LayoutFacebookShareActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.androidapi.resultapi.custom.MultipleLauncherOptions
import com.chenyihong.exampledemo.androidapi.resultapi.custom.PickMultipleMediumContract
import com.chenyihong.exampledemo.androidapi.resultapi.custom.PickSingleMediumContract
import com.chenyihong.exampledemo.base.MimeType
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.*
import com.facebook.share.widget.ShareDialog

const val TAG = "FacebookShare"

class FacebookShareActivity : BaseGestureDetectorActivity<LayoutFacebookShareActivityBinding>() {

    private val singleMediumPicker = registerForActivityResult(PickSingleMediumContract()) { uri ->
        if (uri != null) {
            when (getMimeType(uri)) {
                MimeType.IMAGE_ALL, MimeType.IMAGE_JPEG, MimeType.IMAGE_GIT, MimeType.IMAGE_PNG -> {
                    val sharePhoto = SharePhoto.Builder()
                        //设置分享的bitmap
                        //.setBitmap()
                        //设置分享图片的uri
                        .setImageUrl(uri)
                        //设置标题，必须是用户自己输入的(平台政策 (2.3) 禁止预填内容)
                        .setCaption("")
                        //设置分享的图片是由用户还是应用程序生成的
                        .setUserGenerated(true)
                        .build()

                    val sharePhotoContent = SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build()

                    shareDialog?.show(sharePhotoContent)
                }

                MimeType.VIDEO_All, MimeType.VIDEO_MP4, MimeType.VIDEO_3GP -> {
                    val shareVideo = ShareVideo.Builder()
                        //设置视频Uri
                        .setLocalUrl(uri)
                        .build()

                    val shareVideoContent = ShareVideoContent.Builder()
                        .setVideo(shareVideo)
                        //设置视频标题
                        .setContentTitle("Test Share Video")
                        //设置视频描述
                        .setContentDescription("This is a test video to test facebook sharing")
                        .build()

                    shareDialog?.show(shareVideoContent)
                }
            }
        }
    }

    private val pickMultiplePhoto = registerForActivityResult(PickMultipleMediumContract()) { uriList ->
        if (!uriList.isNullOrEmpty()) {
            val sharePhotos = ArrayList<SharePhoto>()
            uriList.forEach {
                val sharePhoto = SharePhoto.Builder()
                    //设置分享的bitmap
                    //.setBitmap()
                    //设置分享图片的uri
                    .setImageUrl(it)
                    //设置标题，必须是用户自己输入的(平台政策 (2.3) 禁止预填内容)
                    .setCaption("Test Share photo")
                    //设置分享的图片是由用户还是应用程序生成的
                    .setUserGenerated(true)
                    .build()

                sharePhotos.add(sharePhoto)
            }

            val sharePhotoContent = SharePhotoContent.Builder()
                .addPhotos(sharePhotos)
                .build()

            shareDialog?.show(sharePhotoContent)
        }
    }

    private val pickMultipleMedium = registerForActivityResult(PickMultipleMediumContract()) { uriList ->
        if (uriList.isNotEmpty()) {
            val multipleMedium = ArrayList<ShareMedia<*, *>>()
            uriList.forEach {
                when (getMimeType(it)) {
                    MimeType.IMAGE_ALL, MimeType.IMAGE_JPEG, MimeType.IMAGE_GIT, MimeType.IMAGE_PNG -> {
                        val sharePhoto = SharePhoto.Builder()
                            .setImageUrl(it)
                            .setUserGenerated(true)
                            .build()
                        multipleMedium.add(sharePhoto)
                    }

                    MimeType.VIDEO_All, MimeType.VIDEO_MP4, MimeType.VIDEO_3GP -> {
                        val shareVideo = ShareVideo.Builder()
                            //设置视频Uri
                            .setLocalUrl(it)
                            .build()
                        multipleMedium.add(shareVideo)
                    }
                }
            }
            if (multipleMedium.isNotEmpty()) {
                val shareMultipleMedium = ShareMediaContent.Builder()
                    .addMedia(multipleMedium)
                    .build()
                shareDialog?.show(shareMultipleMedium)
            }
        }
    }

    private var shareDialog: ShareDialog? = null

    private lateinit var callbackManager: CallbackManager

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutFacebookShareActivityBinding {
        return LayoutFacebookShareActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)
        shareDialog?.registerCallback(callbackManager, object : FacebookCallback<Sharer.Result> {
            override fun onSuccess(result: Sharer.Result) {
                Log.i(TAG, "share success postId:${result.postId}")
            }

            override fun onCancel() {
                Log.i(TAG, "share cancel")
            }

            override fun onError(error: FacebookException) {
                Log.i(TAG, "share failed error:${error.message}")
            }
        })

        binding.includeTitle.tvTitle.text = "Facebook Share"
        binding.btnShareLink.setOnClickListener {
            val shareLinkContent = ShareLinkContent.Builder()
                //设置分享链接
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                //设置引文
                .setQuote("Connect on a global scale")
                //设置话题标签
                .setShareHashtag(ShareHashtag.Builder()
                    .setHashtag("#ConnectTheWorld")
                    .build())
                .build()

            shareDialog?.show(shareLinkContent)
        }
        binding.btnShareSingleVideo.setOnClickListener {
            singleMediumPicker.launch(MimeType.VIDEO_All)
        }
        binding.btnShareSinglePicture.setOnClickListener {
            singleMediumPicker.launch(null)
        }
        binding.btnShareMultiplePicture.setOnClickListener {
            pickMultiplePhoto.launch(MultipleLauncherOptions(null, 6))
        }
        binding.btnShareMultipleMedium.setOnClickListener {
            pickMultipleMedium.launch(MultipleLauncherOptions(MimeType.ALL, 6))
        }
    }

    private fun getMimeType(uri: Uri): String {
        return contentResolver.getType(uri) ?: ""
    }
}