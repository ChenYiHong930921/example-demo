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

    var itemClickCallback: ItemClickCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 通过viewType判断该使用什么布局，1为视频item，2为普通item
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
        // 设置item项的点击事件
        holder.itemView.setOnClickListener {
            itemClickCallback?.onItemClick(containerData[position])
        }
        when (holder) {
            is VideoItemViewHolder -> {
                containerData[position].run {
                    // 加载第一帧作为封面
                    Glide.with(holder.itemView.context).setDefaultRequestOptions(RequestOptions().frame(1).centerCrop()).load(videoUrl).into(holder.itemViewBinding.ivVideoCover)

                    holder.itemViewBinding.playerView.player?.run {
                        if (pauseVideo) {
                            // 暂停播放
                            holder.pauseVideo()
                        } else {
                            // 开始播放
                            videoUrl?.let { newUrl ->
                                // 使用播放链接获取缓存的播放进度，没有的话从0开始播放
                                setMediaItem(MediaItem.fromUri(newUrl), holder.mediaProgress[newUrl] ?: 0L)
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
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is VideoItemViewHolder) {
            // 当View从Window中被移除时暂停播放视频
            holder.pauseVideo()
        }
    }

    fun setNewData(newData: ArrayList<ExampleListEntity>?) {
        val currentItemCount = itemCount
        if (currentItemCount != 0) {
            containerData.clear()
            notifyItemRangeRemoved(0, currentItemCount)
        }
        if (!newData.isNullOrEmpty()) {
            containerData.addAll(newData)
            notifyItemRangeChanged(0, itemCount)
        }
    }

    fun notifyVideoItemStatus(pauseVideo: Boolean) {
        // 设置是否暂停播放，更新视频 item
        this.pauseVideo = pauseVideo
        containerData.forEach {
            if (it.type == 1) {
                notifyItemChanged(containerData.indexOf(it))
            }
        }
    }

    interface ItemClickCallback {
        fun onItemClick(data: ExampleListEntity)
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
                    itemViewBinding.playerView.player?.run {
                        // 暂停时根据视频链接记录播放的进度
                        currentMediaItem?.localConfiguration?.uri?.toString()?.let { uri -> mediaProgress[uri] = currentPosition }
                    }
                }
            }
        }

        init {
            // 配置ExoPlayer到PlayerView
            itemViewBinding.playerView.player = exoPlayer
            itemViewBinding.playerView.player?.run {
                removeListener(videoListener)
                addListener(videoListener)
                repeatMode = Player.REPEAT_MODE_ALL
            }
        }

        fun pauseVideo() {
            // 暂停播放视频
            itemViewBinding.playerView.onPause()
            exoPlayer.pause()
        }
    }

    class NormalItemViewHolder(val itemViewBinding: LayoutMedia3ListNormalItemBinding) : RecyclerView.ViewHolder(itemViewBinding.root)
}