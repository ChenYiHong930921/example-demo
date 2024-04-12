package com.chenyihong.exampledemo.androidapi.media3

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheSpan
import androidx.media3.datasource.cache.CacheWriter
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.NavigableSet
import java.util.concurrent.ConcurrentHashMap

@UnstableApi
class CacheController(context: Context) {

    private val cache: Cache
    private val cacheDataSourceFactory: CacheDataSource.Factory
    private val cacheDataSource: CacheDataSource

    private val cacheTask: ConcurrentHashMap<String, CacheWriter> = ConcurrentHashMap()

    init {
        // 设置缓存目录和缓存机制，如果不需要清除缓存可以使用NoOpCacheEvictor
        cache = SimpleCache(File(if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) else context.filesDir, "example_media_cache"),
            LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024),
            ExampleDatabaseProvider(context)
        )
        // 根据缓存目录创建缓存数据源
        cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            // 设置上游数据源，缓存未命中时通过此获取数据
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true))
        cacheDataSource = cacheDataSourceFactory.createDataSource()
    }

    companion object {

        @Volatile
        private var cacheController: CacheController? = null

        fun init(context: Context) {
            if (cacheController == null) {
                synchronized(CacheController::class.java) {
                    if (cacheController == null) {
                        cacheController = CacheController(context)
                    }
                }
            }
        }

        fun cacheMedia(mediaUrl: String, key: String = "") {
            cacheController?.run {
                // 创建CacheWriter缓存数据
                val dataSpecBuilder = DataSpec.Builder()
                    // 设置资源链接
                    .setUri(mediaUrl)
                    // 设置需要缓存的大小（可以只缓存一部分）
                    .setLength((getMediaResourceSize(mediaUrl) * 0.1).toLong())
                if (key.isNotEmpty()) {
                    dataSpecBuilder.setKey(key)
                }
                CacheWriter(cacheDataSource, dataSpecBuilder.build(), null) { requestLength, bytesCached, newBytesCached ->
                    Log.i("-,-,-", "requestLength:$requestLength, bytesCached$bytesCached, newBytesCached:$newBytesCached")
                    // 缓冲进度变化时回调
                    // requestLength 请求总大小
                    // bytesCached 已缓冲的字节数
                    // newBytesCached 新缓冲的字节数
                }.let { cacheWriter ->
                    cacheWriter.cache()
                    cacheTask[mediaUrl] = cacheWriter
                }
            }
        }

        fun cancelCache(mediaUrl: String) {
            // 取消缓存
            cacheController?.cacheTask?.get(mediaUrl)?.cancel()
        }

        fun getMediaSourceFactory(): MediaSource.Factory? {
            var mediaSourceFactory: MediaSource.Factory? = null
            cacheController?.run {
                // 创建逐步加载数据的数据源
                mediaSourceFactory = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
            }
            return mediaSourceFactory
        }

        fun removeCache(key: String) {
            cacheController?.cache?.removeResource(key)
        }

        fun transformCacheToVideo(context: Context, key: String = ""): File? {
            var transformFile: File? = null
            cacheController?.cache?.run {
                key.ifEmpty {
                    keys.firstOrNull()
                }?.let {
                    transformFile = cacheController?.transformCacheSpanToMp4(context, getCachedSpans(it))
                }
            }
            return transformFile
        }

        fun release() {
            cacheController?.cacheTask?.values?.forEach { it.cancel() }
            cacheController?.cache?.release()
        }
    }

    // 获取媒体资源的大小
    private fun getMediaResourceSize(mediaUrl: String): Long {
        try {
            val connection = URL(mediaUrl).openConnection() as HttpURLConnection
            // 请求方法设置为HEAD，只获取请求头
            connection.requestMethod = "HEAD"
            connection.connect()
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                return connection.getHeaderField("Content-Length").toLong()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0L
    }

    private fun transformCacheSpanToMp4(context: Context, cacheSpans: NavigableSet<CacheSpan>): File {
        val targetMp4File = File(if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        } else {
            context.filesDir
        }, "testVideo.mp4")
        if (!targetMp4File.exists()) {
            targetMp4File.createNewFile()
            FileOutputStream(targetMp4File, true).use { output ->
                cacheSpans.forEach { cacheSpan ->
                    FileInputStream(cacheSpan.file).let { input ->
                        val buffer = ByteArray(1024)
                        var length: Int
                        while (input.read(buffer).also { length = it } > 0) {
                            output.write(buffer, 0, length)
                        }
                        input.close()
                    }
                }
            }
        }
        return targetMp4File
    }
}