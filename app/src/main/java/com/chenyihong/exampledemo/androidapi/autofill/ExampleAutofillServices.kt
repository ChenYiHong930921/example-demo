package com.chenyihong.exampledemo.androidapi.autofill

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class ExampleAutofillServices : AutofillService() {

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal, callback: FillCallback) {
        val fillContexts = request.fillContexts
        val structure = fillContexts.last().structure
        for (index in 0 until structure.windowNodeCount) {
            val windowNode = structure.getWindowNodeAt(index)
            Log.i(TAG, "${windowNode.displayId}, ${windowNode.title}")
            windowNode.rootViewNode?.let { parseViewNode(it) }
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        val fillContexts = request.fillContexts
        val structure = fillContexts.last().structure
        parseStructure(structure)
        callback.onSuccess()
    }

    private fun parseStructure(structure: AssistStructure) {
        for (index in 0 until structure.windowNodeCount) {
            val windowNode = structure.getWindowNodeAt(index)
            Log.i(TAG, "${windowNode.displayId}, ${windowNode.title}")
            windowNode.rootViewNode?.let { parseViewNode(it) }
        }
    }

    private fun parseViewNode(viewNode: ViewNode) {
        viewNode.run {
            Log.i(TAG, "rootViewNode autofillId:${autofillId}, autofillHints:$autofillHints, autofillType:$autofillType, autofillValue:$autofillValue, childCount:$childCount")
            for (childIndex in 0 until childCount) {
                parseViewNode(getChildAt(childIndex))
            }
        }
    }
}