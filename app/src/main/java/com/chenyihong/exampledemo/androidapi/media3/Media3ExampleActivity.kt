package com.chenyihong.exampledemo.androidapi.media3

import android.annotation.SuppressLint
import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutMedia3ExampleActivityBinding
import com.google.common.util.concurrent.ListenableFuture

class Media3ExampleActivity : BaseGestureDetectorActivity<LayoutMedia3ExampleActivityBinding>() {

    private var backgroundPlay = false

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val mediaController: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            // 播放状态变化回调
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_IDLE -> {
                    //播放器停止时的状态
                }

                Player.STATE_BUFFERING -> {
                    // 正在缓冲数据
                }

                Player.STATE_READY -> {
                    // 可以开始播放
                }

                Player.STATE_ENDED -> {
                    // 播放结束
                }
            }

        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            // 获取播放错误信息
        }
    }

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutMedia3ExampleActivityBinding {
        return LayoutMedia3ExampleActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backgroundPlay = intent.getBooleanExtra("backgroundPlay", false)

        binding.includeTitle.tvTitle.text = "Media3 Example"

        // 播放hls示例代码
        /*val testMediaSource = HlsMediaSource.Factory(DefaultDataSource.Factory(this))
            .createMediaSource(MediaItem.fromUri(File(if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            } else {
                filesDir
            }, "TestVideo/index.m3u8").toUri()))*/

        if (!backgroundPlay) {
            // 创建ExoPlayer，配置到PlayerView中
            val exoPlayerBuilder = ExoPlayer.Builder(this)
            CacheController.getMediaSourceFactory()?.let { exoPlayerBuilder.setMediaSourceFactory(it) }
            binding.playView.player = exoPlayerBuilder.build()
            binding.playView.player?.run {
                // 设置播放监听
                addListener(playerListener)
                // 设置重复模式
                // Player.REPEAT_MODE_ALL 无限重复
                // Player.REPEAT_MODE_ONE 重复一次
                // Player.REPEAT_MODE_OFF 不重复
                repeatMode = Player.REPEAT_MODE_ALL
                // 设置当缓冲完毕后直接播放视频
                playWhenReady = true
            }
        }
        binding.btnPlaySingleVideo.setOnClickListener {
            binding.playView.player?.run {
                // 停止之前播放的视频
                stop()

                // 播放hls示例代码
                /*if (this is ExoPlayer) {
                    setMediaSource(testMediaSource)
                }*/

                //设置单个资源
                setMediaItem(MediaItem.fromUri("https://minigame.vip/Uploads/images/2021/09/18/1631951892_page_img.mp4"))
                // 开始缓冲
                prepare()
            }
        }
        binding.btnPlayMultiVideo.setOnClickListener {
            binding.playView.player?.run {
                // 停止之前播放的视频
                stop()
                // 设置多个资源，当一个视频播完后自动播放下一个
                setMediaItems(arrayListOf(
                    MediaItem.fromUri("https://minigame.vip/Uploads/images/2021/09/18/1631951892_page_img.mp4"),
                    MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-1/mp4/dizzy-with-tx3g.mp4")
                ))
                // 开始缓冲
                prepare()
            }
        }
        binding.btnScreenshot.setOnClickListener {
            (binding.playView.videoSurfaceView as? TextureView)?.bitmap?.let { binding.ivScreenshotContainer.setImageBitmap(it) }
        }
    }

    @UnstableApi
    private fun initController() {
        controllerFuture = MediaController.Builder(this, SessionToken(this, ComponentName(this, ExamplePlaybackService::class.java)))
            .buildAsync()
        controllerFuture.addListener({
            binding.root.post {
                mediaController?.let {
                    binding.playView.player = it.apply {
                        // 设置播放监听
                        addListener(playerListener)
                        // 设置重复模式
                        // Player.REPEAT_MODE_ALL 无限重复
                        // Player.REPEAT_MODE_ONE 重复一次
                        // Player.REPEAT_MODE_OFF 不重复
                        repeatMode = Player.REPEAT_MODE_ALL
                        // 设置当缓冲完毕后直接播放视频
                        playWhenReady = true
                    }
                }
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @UnstableApi
    override fun onStart() {
        super.onStart()
        if (backgroundPlay) {
            initController()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!backgroundPlay) {
            // 恢复播放
            binding.playView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!backgroundPlay) {
            // 暂停播放
            binding.playView.onPause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (backgroundPlay) {
            binding.playView.player = null
            MediaController.releaseFuture(controllerFuture)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放播放器资源
        binding.playView.player?.release()
        binding.playView.player = null
    }
}