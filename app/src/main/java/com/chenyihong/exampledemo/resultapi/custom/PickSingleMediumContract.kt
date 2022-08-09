package com.chenyihong.exampledemo.resultapi.custom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.chenyihong.exampledemo.share.MimeType

class PickSingleMediumContract(private val mineType: String = MimeType.IMAGE_ALL) : ActivityResultContract<String, Uri>() {

    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(Intent.ACTION_PICK)
            .setType(mineType)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }

}