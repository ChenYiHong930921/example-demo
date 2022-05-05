package com.chenyihong.exampledemo.resultapi.custom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class PickMultiplePhotoContract : ActivityResultContract<String, List<Uri>>() {

    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(Intent.ACTION_PICK)
            .setType("image/*")
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
        if (intent.data != null) {
            resultSet.add(intent.data!!)
        }
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