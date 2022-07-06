package com.chenyihong.exampledemo.share.tripartiteshare

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutFacebookShareActivityBinding
import com.chenyihong.exampledemo.resultapi.custom.PickMultiplePhotoContract
import com.chenyihong.exampledemo.resultapi.custom.PickSinglePhotoContract
import com.chenyihong.exampledemo.share.MimeType
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.*
import com.facebook.share.widget.ShareDialog


const val TAG = "FacebookShare"

class FacebookShareActivity : AppCompatActivity() {

    private val pickPhoto = registerForActivityResult(PickSinglePhotoContract()) { uri ->
        if (uri != null) {
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
    }

    private val pickMultiplePhoto = registerForActivityResult(PickMultiplePhotoContract()) { uri ->
        if (!uri.isNullOrEmpty()) {
            multiplePhotoList.addAll(uri)
            val sharePhotos = ArrayList<SharePhoto>()
            uri.forEach {
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

    private val pickVideo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            multipleVideoList.add(uri)
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

    private var multiplePhotoList = ArrayList<Uri>()
    private var multipleVideoList = ArrayList<Uri>()

    private var shareDialog: ShareDialog? = null

    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<LayoutFacebookShareActivityBinding>(this, R.layout.layout_facebook_share_activity)

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
            pickVideo.launch(MimeType.VIDEO_All)
        }
        binding.btnShareSinglePicture.setOnClickListener {
            pickPhoto.launch(null)
        }
        binding.btnShareMultiplePicture.setOnClickListener {
            pickMultiplePhoto.launch(null)
        }
        binding.btnShareMultipleMedium.setOnClickListener {
            val multipleMedium = ArrayList<ShareMedia<*, *>>()

            multiplePhotoList.forEach {
                val sharePhoto = SharePhoto.Builder()
                    .setImageUrl(it)
                    .setUserGenerated(true)
                    .build()
                multipleMedium.add(sharePhoto)
            }
            multipleVideoList.forEach {
                val shareVideo = ShareVideo.Builder()
                    //设置视频Uri
                    .setLocalUrl(it)
                    .build()

                multipleMedium.add(shareVideo)
            }

            val shareMultipleMedium = ShareMediaContent.Builder()
                .addMedia(multipleMedium)
                .build()

            shareDialog?.show(shareMultipleMedium)
        }
    }
}