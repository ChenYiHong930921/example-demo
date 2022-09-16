package com.chenyihong.exampledemo.resultapi.custom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import com.chenyihong.exampledemo.share.MimeType

class PickSingleMediumContract : ActivityResultContract<String?, Uri?>() {

    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) MediaStore.ACTION_PICK_IMAGES else Intent.ACTION_PICK)
            .setType(if (input.isNullOrEmpty() || input.isBlank()) MimeType.ALL else input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }

}