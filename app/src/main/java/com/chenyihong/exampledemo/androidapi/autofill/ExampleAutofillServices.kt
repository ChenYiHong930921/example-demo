package com.chenyihong.exampledemo.androidapi.autofill

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveInfo
import android.service.autofill.SaveRequest
import android.util.Log
import android.view.View
import android.view.autofill.AutofillId
import android.widget.RemoteViews
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class ExampleAutofillServices : AutofillService() {

    private val fillId = HashMap<String, AutofillId>()

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal, callback: FillCallback) {
        Log.i(TAG, "onFillRequest")
        parseStructure(request.fillContexts.last().structure)
        val usernameId = fillId[View.AUTOFILL_HINT_USERNAME]
        val passwordId = fillId[View.AUTOFILL_HINT_PASSWORD]
        if (usernameId != null && passwordId != null) {
            RemoteViews(packageName, android.R.layout.simple_list_item_1).let {
                callback.onSuccess(FillResponse.Builder()
                    /*.addDataset(Dataset.Builder()
                        .setValue(usernameId, null, it)
                        .setValue(passwordId, null, it)
                        .build())*/
                    .setSaveInfo(SaveInfo.Builder(SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                        arrayOf(usernameId, passwordId))
                        .build())
                    .build())
            }
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        Log.i(TAG, "onSaveRequest")
        parseStructure(request.fillContexts.last().structure)
    }

    private fun parseStructure(structure: AssistStructure) {
        Log.i(TAG, "activityComponent:${structure.activityComponent}, windowNodeCount:${structure.windowNodeCount}")
        fillId.clear()
        for (index in 0 until structure.windowNodeCount) {
            val windowNode = structure.getWindowNodeAt(index)
            Log.i(TAG, "${windowNode.displayId}, ${windowNode.title}")
            windowNode.rootViewNode?.let { parseViewNode(it) }
        }
    }

    private fun parseViewNode(viewNode: ViewNode) {
        viewNode.run {
            Log.i(TAG, "rootViewNode autofillId:${autofillId}, autofillType:$autofillType, childCount:$childCount")
            autofillId?.let { id ->
                autofillHints?.let {
                    for (index in it.indices) {
                        it[index]?.let { hint ->
                            Log.i(TAG, "rootViewNode autofillHints:$hint")
                            if (hint == View.AUTOFILL_HINT_USERNAME || hint == View.AUTOFILL_HINT_PASSWORD) {
                                fillId[hint] = id
                            }
                        }
                    }
                }
            }
            Log.i(TAG, "rootViewNode autofillValue:$autofillValue textValue:${autofillValue?.textValue}")
            for (childIndex in 0 until childCount) {
                parseViewNode(getChildAt(childIndex))
            }
        }
    }
}