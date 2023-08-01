package com.chenyihong.exampledemo.androidapi.media3

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider

@UnstableApi
class ExampleDatabaseProvider(
    context: Context,
    databaseName: String = "example_exoplayer_internal.db",
    version: Int = 1
) : SQLiteOpenHelper(context.applicationContext, databaseName, null, version), DatabaseProvider {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}