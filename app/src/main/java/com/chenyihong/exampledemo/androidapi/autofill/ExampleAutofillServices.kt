package com.chenyihong.exampledemo.androidapi.autofill

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveInfo
import android.service.autofill.SaveRequest
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.chenyihong.exampledemo.androidapi.setting.ExampleDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

@RequiresApi(Build.VERSION_CODES.O)
class ExampleAutofillServices : AutofillService() {

    private val fillId = HashMap<String, AutofillId>()

    private val saveInputValues = HashMap<String, String>()

    override fun onCreate() {
        super.onCreate()
        ExampleDataStore.coroutineScope = CoroutineScope(Dispatchers.IO)
    }

    override fun onDestroy() {
        super.onDestroy()
        ExampleDataStore.coroutineScope?.cancel()
        ExampleDataStore.coroutineScope = null
    }

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal, callback: FillCallback) {
        Log.i(TAG, "onFillRequest")
        // 触发自动填充时回调
        request.fillContexts.last().structure.let { structure ->
            parseStructure(structure)
            val usernameId = fillId[View.AUTOFILL_HINT_USERNAME]
            val passwordId = fillId[View.AUTOFILL_HINT_PASSWORD]
            if (usernameId != null && passwordId != null) {
                val fillResponseBuilder = FillResponse.Builder()
                val usernameSavedValue = ExampleDataStore.getString("${structure.activityComponent.shortClassName}_${View.AUTOFILL_HINT_USERNAME}", "")
                val passwordSavedValue = ExampleDataStore.getString("${structure.activityComponent.shortClassName}_${View.AUTOFILL_HINT_PASSWORD}", "")
                Log.i(TAG, "usernameSavedValue:$usernameSavedValue, passwordSavedValue:$passwordSavedValue")
                if (TextUtils.isEmpty(usernameSavedValue) && TextUtils.isEmpty(passwordSavedValue)) {
                    fillResponseBuilder.setSaveInfo(SaveInfo.Builder(SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                        arrayOf(usernameId, passwordId))
                        .build())
                } else {
                    val dataSetBuilder = Dataset.Builder()
                    var saveInfo: SaveInfo? = null
                    if (!TextUtils.isEmpty(usernameSavedValue)) {
                        val usernamePresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
                            setTextViewText(android.R.id.text1, "Account")
                        }
                        dataSetBuilder.setValue(usernameId, AutofillValue.forText(usernameSavedValue), usernamePresentation)
                        if (TextUtils.isEmpty(passwordSavedValue)) {
                            saveInfo = SaveInfo.Builder(SaveInfo.SAVE_DATA_TYPE_PASSWORD, arrayOf(passwordId)).build()
                        }
                    }
                    if (!TextUtils.isEmpty(passwordSavedValue)) {
                        val passwordPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
                            setTextViewText(android.R.id.text1, "Password")
                        }
                        dataSetBuilder.setValue(passwordId, AutofillValue.forText(passwordSavedValue), passwordPresentation)
                        if (TextUtils.isEmpty(usernameSavedValue)) {
                            saveInfo = SaveInfo.Builder(SaveInfo.SAVE_DATA_TYPE_USERNAME, arrayOf(usernameId)).build()
                        }
                    }
                    fillResponseBuilder.addDataset(dataSetBuilder.build())
                    saveInfo?.let { fillResponseBuilder.setSaveInfo(it) }
                }
                callback.onSuccess(fillResponseBuilder.build())
            }
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        Log.i(TAG, "onSaveRequest")
        // 触发保存时回调
        request.fillContexts.last().structure.let { structure ->
            saveInputValues.clear()
            parseStructure(structure, true)
            if (saveInputValues.isNotEmpty()) {
                saveInputValues.entries.forEach {
                    Log.i(TAG, "saveInputValues key:${it.key}, value:${it.value}")
                    ExampleDataStore.putString("${structure.activityComponent.shortClassName}_${it.key}", it.value)
                }
                callback.onSuccess()
            } else {
                callback.onFailure("fill value not found")
            }
        }
    }

    private fun parseStructure(structure: AssistStructure, fromSaveRequest: Boolean = false) {
        Log.i(TAG, "activityComponent:${structure.activityComponent}, windowNodeCount:${structure.windowNodeCount}")
        fillId.clear()
        for (index in 0 until structure.windowNodeCount) {
            val windowNode = structure.getWindowNodeAt(index)
            Log.i(TAG, "${windowNode.displayId}, ${windowNode.title}")
            windowNode.rootViewNode?.let { parseViewNode(it, fromSaveRequest) }
        }
    }

    private fun parseViewNode(viewNode: ViewNode, fromSaveRequest: Boolean) {
        viewNode.run {
            Log.i(TAG, "rootViewNode autofillId:${autofillId}, autofillType:$autofillType, childCount:$childCount")
            if (autofillHints.isNullOrEmpty()) {
                // 如果应用没有提供autofillHints，可以根据viewNode.getText()或者viewNode.getHint()来判断需要填充什么类型的数据
                Log.i(TAG, "rootViewNode text:${viewNode.text}, hint:${viewNode.hint}")
            } else {
                autofillHints?.forEach { hint ->
                    Log.i(TAG, "rootViewNode autofillHints:$hint")
                    if (hint == View.AUTOFILL_HINT_USERNAME || hint == View.AUTOFILL_HINT_PASSWORD) {
                        autofillId?.let { id -> fillId[hint] = id }
                        if (fromSaveRequest) {
                            autofillValue?.let { value -> saveInputValues[hint] = value.textValue.toString() }
                        }
                    }
                }
            }
            Log.i(TAG, "rootViewNode autofillValue:$autofillValue textValue:${autofillValue?.textValue}")
            for (childIndex in 0 until childCount) {
                parseViewNode(getChildAt(childIndex), fromSaveRequest)
            }
        }
    }
}