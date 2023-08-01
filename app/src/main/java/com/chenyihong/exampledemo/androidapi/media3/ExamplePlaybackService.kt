package com.chenyihong.exampledemo.androidapi.media3

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

@UnstableApi
class ExamplePlaybackService : MediaSessionService() {

    private var exoPlayer: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        // 创建ExoPlayer
        val exoPlayerBuilder = ExoPlayer.Builder(this)
        // 设置缓存数据源
        CacheController.getMediaSourceFactory()?.let { exoPlayerBuilder.setMediaSourceFactory(it) }
        exoPlayer = exoPlayerBuilder.build()
        // 基于已创建的ExoPlayer创建MediaSession
        exoPlayer?.let { mediaSession = MediaSession.Builder(this, it).build() }
    }

    override fun onDestroy() {
        // 释放相关实例
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }
}