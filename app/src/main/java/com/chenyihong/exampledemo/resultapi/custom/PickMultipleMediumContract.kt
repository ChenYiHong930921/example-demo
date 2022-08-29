package com.chenyihong.exampledemo.resultapi.custom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import com.chenyihong.exampledemo.share.MimeType

class PickMultipleMediumContract : ActivityResultContract<MultipleLauncherOptions?, List<Uri>>() {

    override fun createIntent(context: Context, input: MultipleLauncherOptions?): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val maxLimit = MediaStore.getPickImagesMaxLimit()
            val inputMaxCount = input?.maxCount ?: 0
            val finalMaxCount = if (inputMaxCount != 0 && inputMaxCount < maxLimit) inputMaxCount else maxLimit
            Intent(MediaStore.ACTION_PICK_IMAGES)
                .setType(if (input?.mimeType.isNullOrEmpty() || input?.mimeType.isNullOrBlank()) MimeType.ALL else input?.mimeType)
                .putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, finalMaxCount)
        } else {
            Intent(Intent.ACTION_PICK)
                .setType(if (input?.mimeType.isNullOrEmpty() || input?.mimeType.isNullOrBlank()) MimeType.ALL else input?.mimeType)
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        return if (intent == null || resultCode != Activity.RESULT_OK) {
            emptyList()
        } else {
            getClipDataUris(intent)
        }
    }

    private fun getClipDataUris(intent: Intent): List<Uri> {
        val resultSet = LinkedHashSet<Uri>()
        intent.data?.let { resultSet.add(it) }
        val clipData = intent.clipData
        if (clipData == null && resultSet.isEmpty()) {
            return emptyList()
        } else if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                val uri = clipData.getItemAt(i).uri
                if (uri != null) {
                    resultSet.add(uri)
                }
            }
        }
        return ArrayList(resultSet)
    }
}