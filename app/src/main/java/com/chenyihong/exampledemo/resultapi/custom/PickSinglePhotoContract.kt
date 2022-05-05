package com.chenyihong.exampledemo.resultapi.custom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class PickSinglePhotoContract : ActivityResultContract<String, Uri>() {

    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(Intent.ACTION_PICK)
            .setType("image/*")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }

}