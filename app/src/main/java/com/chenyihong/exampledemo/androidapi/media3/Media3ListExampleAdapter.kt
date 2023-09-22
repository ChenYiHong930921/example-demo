package com.chenyihong.exampledemo.androidapi.media3

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chenyihong.exampledemo.databinding.LayoutMedia3ListNormalItemBinding
import com.chenyihong.exampledemo.databinding.LayoutMedia3ListVideoItemBinding
import java.util.concurrent.ConcurrentHashMap

@UnstableApi
class Media3ListExampleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val containerData = ArrayList<ExampleListEntity>()

    private var pauseVideo = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            VideoItemViewHolder(LayoutMedia3ListVideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            NormalItemViewHolder(LayoutMedia3ListNormalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemCount(): Int {
        return containerData.size
    }

    override fun getItemViewType(position: Int): Int {
        return containerData[position].type
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VideoItemViewHolder -> {
                containerData[position].run {
                    // 加载第一帧作为封面
                    Glide.with(holder.itemView.context)
                        .setDefaultRequestOptions(RequestOptions()
                            .frame(1)
                            .centerCrop())
                        .load(videoUrl)
                        .into(holder.itemViewBinding.ivVideoCover)

                    holder.itemViewBinding.playerView.player?.run {
                        if (pauseVideo) {
                            // 暂停播放
                            holder.changeVideoStatus(true)
                        } else {
                            // 开始播放
                            videoUrl?.let { newUrl ->
                                // 判断当前播放器链接与当前链接是否一致，是则跳转到记录的播放进度，否则从0开始播放
                                setMediaItem(MediaItem.fromUri(newUrl), if (currentMediaItem?.localConfiguration?.uri?.toString() != newUrl) 0L else (holder.mediaProgress[newUrl] ?: 0L))
                            }
                            playWhenReady = true
                            prepare()
                        }
                    }
                }
            }

            is NormalItemViewHolder -> holder.itemViewBinding.tvTextContent.text = "${containerData[position].itemText ?: "Normal item"} $position"
        }
    }

    @UnstableApi
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is VideoItemViewHolder) {
            // 当View添加到Window中并且此时不为暂停状态时播放视频
            if (!pauseVideo) {
                holder.changeVideoStatus(false)
            }
        }
    }

    @UnstableApi
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is VideoItemViewHolder) {
            // 当View从Window中被移除时暂停播放视频
            holder.changeVideoStatus(true)
        }
    }

    class VideoItemViewHolder(val itemViewBinding: LayoutMedia3ListVideoItemBinding) : RecyclerView.ViewHolder(itemViewBinding.root) {
        private val exoPlayer: ExoPlayer = ExoPlayer.Builder(itemView.context).apply {
            CacheController.getMediaSourceFactory()?.let { setMediaSourceFactory(it) }
        }.build()

        val mediaProgress = ConcurrentHashMap<String, Long>()

        private val videoListener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    // 开始播放时隐藏封面图
                    itemViewBinding.ivVideoCover.visibility = View.GONE
                } else {
                    // 暂停时记录播放的进度
                    itemViewBinding.playerView.player?.run {
                        currentMediaItem?.localConfiguration?.uri?.toString()?.let { uri -> mediaProgress[uri] = currentPosition }
                    }
                }
            }
        }

        init {
            itemViewBinding.playerView.player = exoPlayer
            itemViewBinding.playerView.player?.run {
                removeListener(videoListener)
                addListener(videoListener)
                repeatMode = Player.REPEAT_MODE_ALL
            }
        }

        fun changeVideoStatus(pause: Boolean) {
            if (pause) {
                itemViewBinding.playerView.onPause()
                exoPlayer.pause()
            } else {
                itemViewBinding.playerView.onResume()
                exoPlayer.play()
            }
        }
    }

    class NormalItemViewHolder(val itemViewBinding: LayoutMedia3ListNormalItemBinding) : RecyclerView.ViewHolder(itemViewBinding.root)
}