package com.chenyihong.exampledemo.androidapi.media3

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutMedia3ExampleActivityBinding

class Media3ExampleActivity : BaseGestureDetectorActivity<LayoutMedia3ExampleActivityBinding>() {

    /*private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val mediaController: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null*/

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutMedia3ExampleActivityBinding {
        return LayoutMedia3ExampleActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*controllerFuture = MediaController.Builder(this, MediaSession.Builder(this, ExoPlayer.Builder(this).build())
            .build()
            .token)
            .buildAsync()
        controllerFuture.addListener({
            binding.root.post {
                mediaController?.let {
                    binding.playView.player = it.apply {
                        setMediaItem(MediaItem.fromUri("https://minigame.vip/Uploads/images/2021/09/18/1631951892_page_img.mp4").apply {

                        })
                        addListener(object : Player.Listener {
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
                        })
                        playWhenReady = true
                        prepare()
                    }
                }
            }
        }, ContextCompat.getMainExecutor(this))*/
        binding.includeTitle.tvTitle.text = "Media3 Example"
        binding.playView.player = ExoPlayer.Builder(this)
            .build()
        binding.playView.player?.run {
            setMediaItems(arrayListOf(
                MediaItem.fromUri("https://minigame.vip/Uploads/images/2021/09/18/1631951892_page_img.mp4"),
                MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-1/mp4/dizzy-with-tx3g.mp4")
            ))
            addListener(object : Player.Listener {
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
            })
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            prepare()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.playView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.playView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.playView.player?.release()
        binding.playView.player = null
    }
}