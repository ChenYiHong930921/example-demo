package com.chenyihong.exampledemo.androidapi.targetsdk

import android.app.job.JobParameters
import android.app.job.JobService

class ExampleJobServices : JobService() {

    private var startTime = 0L
    private var currentTime = 0L

    override fun onStartJob(params: JobParameters?): Boolean {
        startTime = System.currentTimeMillis()
        currentTime = System.currentTimeMillis()
        while (currentTime - startTime <= 10 * 1000) {
            currentTime = System.currentTimeMillis()
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}