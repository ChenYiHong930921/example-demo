package com.chenyihong.exampledemo.customview.view

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutVolumeContollerDialogBinding
import com.chenyihong.exampledemo.utils.DensityUtil

class VolumeControllerDialog : DialogFragment() {

    private var binding: LayoutVolumeContollerDialogBinding? = null

    private var currentVolume = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.run {
            setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), android.R.color.transparent))
            decorView.setBackgroundResource(android.R.color.transparent)

            val layoutParams = attributes
            layoutParams.width = DensityUtil.dp2Px(360)
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.gravity = Gravity.CENTER
            attributes = layoutParams
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_volume_contoller_dialog, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val step = 1
        binding?.run {
            btnMute.text = getMuteButtonString(audioManager.isStreamMute(AudioManager.STREAM_MUSIC))
            btnIncreaseVolume.setOnClickListener {
                // 增加音量
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume + step, AudioManager.FLAG_SHOW_UI or AudioManager.FLAG_PLAY_SOUND)
                btnMute.text = getMuteButtonString(audioManager.isStreamMute(AudioManager.STREAM_MUSIC))
            }
            btnReduceVolume.setOnClickListener {
                // 减少音量
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, if (currentVolume - step < 0) 0 else currentVolume - step, AudioManager.FLAG_SHOW_UI or AudioManager.FLAG_PLAY_SOUND)
                btnMute.text = getMuteButtonString(audioManager.isStreamMute(AudioManager.STREAM_MUSIC))
            }
            btnMute.setOnClickListener {
                // 静音或取消静音
                val currentMute = audioManager.isStreamMute(AudioManager.STREAM_MUSIC)
                if (currentVolume == 0) {
                    btnMute.text = getMuteButtonString(true)
                } else {
                    btnMute.text = getMuteButtonString(!currentMute)
                }
                val setVolume = if (currentMute) {
                    currentVolume
                } else {
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                    0
                }
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, setVolume, AudioManager.FLAG_SHOW_UI or AudioManager.FLAG_PLAY_SOUND)
            }
        }
    }

    private fun getMuteButtonString(mute: Boolean): String {
        return if (mute) "UnMute" else "Mute"
    }
}