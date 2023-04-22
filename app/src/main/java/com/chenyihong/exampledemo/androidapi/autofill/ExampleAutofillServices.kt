package com.chenyihong.exampledemo.androidapi.autofill

import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class ExampleAutofillServices : AutofillService() {

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal, callback: FillCallback) {
        val fillContexts = request.fillContexts

    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {

    }
}