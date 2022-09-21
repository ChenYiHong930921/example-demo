package com.chenyihong.exampledemo.setting

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.preference.PreferenceDataStore
import com.chenyihong.exampledemo.ExampleApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object ExampleDataStore : PreferenceDataStore() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ExamplePreferencesDataStore")

    var lifecycleScope: LifecycleCoroutineScope? = null

    override fun putInt(key: String?, value: Int) {
        lifecycleScope?.launch {
            putIntImpl(key, value)
        }
    }

    override fun getInt(key: String?, defValue: Int): Int {
        var getValue: Int
        runBlocking {
            getValue = getIntImpl(key, defValue)
        }
        return getValue
    }

    override fun putLong(key: String?, value: Long) {
        lifecycleScope?.launch {
            putLongImpl(key, value)
        }
    }

    override fun getLong(key: String?, defValue: Long): Long {
        var getValue: Long
        runBlocking {
            getValue = getLongImpl(key, defValue)
        }
        return getValue
    }

    override fun putFloat(key: String?, value: Float) {
        lifecycleScope?.launch {
            putFloatImpl(key, value)
        }
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        var getValue: Float
        runBlocking {
            getValue = getFloatImpl(key, defValue)
        }
        return getValue
    }

    override fun putBoolean(key: String?, value: Boolean) {
        lifecycleScope?.launch {
            putBooleanImpl(key, value)
        }
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        var getValue: Boolean
        runBlocking {
            getValue = getBooleanImpl(key, defValue)
        }
        return getValue
    }

    override fun putString(key: String?, value: String?) {
        lifecycleScope?.launch {
            putStringImpl(key, value)
        }
    }

    override fun getString(key: String?, defValue: String?): String? {
        var getValue: String?
        runBlocking {
            getValue = getStringImpl(key, defValue)
        }
        return getValue
    }

    override fun putStringSet(key: String?, values: MutableSet<String>?) {
        lifecycleScope?.launch {
            putStringSetImpl(key, values)
        }
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String> {
        val getValue = mutableSetOf<String>()
        runBlocking {
            getValue.addAll(getStringSetImpl(key, defValues))
        }
        return getValue
    }

    private suspend fun putIntImpl(key: String?, value: Int?) {
        if (key?.isNotEmpty() == true && value != null) {
            val preferencesKey = intPreferencesKey(key)
            ExampleApplication.exampleContext?.run {
                dataStore.edit {
                    it[preferencesKey] = value
                }
            }
        }
    }

    private suspend fun getIntImpl(key: String?, defaultValue: Int?): Int {
        return if (key?.isNotEmpty() == true) {
            val preferencesKey = intPreferencesKey(key)
            ExampleApplication.exampleContext?.dataStore?.data?.map {
                it[preferencesKey] ?: (defaultValue ?: 0)
            }?.first() ?: 0
        } else {
            0
        }
    }

    private suspend fun putLongImpl(key: String?, value: Long?) {
        if (key?.isNotEmpty() == true && value != null) {
            val preferencesKey = longPreferencesKey(key)
            ExampleApplication.exampleContext?.run {
                dataStore.edit {
                    it[preferencesKey] = value
                }
            }
        }
    }

    private suspend fun getLongImpl(key: String?, defaultValue: Long?): Long {
        return if (key?.isNotEmpty() == true) {
            val preferencesKey = longPreferencesKey(key)
            ExampleApplication.exampleContext?.dataStore?.data?.map {
                it[preferencesKey] ?: (defaultValue ?: 0L)
            }?.first() ?: 0L
        } else {
            0L
        }
    }

    private suspend fun putFloatImpl(key: String?, value: Float?) {
        if (key?.isNotEmpty() == true && value != null) {
            val preferencesKey = floatPreferencesKey(key)
            ExampleApplication.exampleContext?.run {
                dataStore.edit {
                    it[preferencesKey] = value
                }
            }
        }
    }

    private suspend fun getFloatImpl(key: String?, defaultValue: Float?): Float {
        return if (key?.isNotEmpty() == true) {
            val preferencesKey = floatPreferencesKey(key)
            ExampleApplication.exampleContext?.dataStore?.data?.map {
                it[preferencesKey] ?: (defaultValue ?: 0f)
            }?.first() ?: 0f
        } else {
            0f
        }
    }

    private suspend fun putBooleanImpl(key: String?, value: Boolean?) {
        if (key?.isNotEmpty() == true && value != null) {
            val preferencesKey = booleanPreferencesKey(key)
            ExampleApplication.exampleContext?.run {
                dataStore.edit {
                    it[preferencesKey] = value
                }
            }
        }
    }

    private suspend fun getBooleanImpl(key: String?, defaultValue: Boolean?): Boolean {
        return if (key?.isNotEmpty() == true) {
            val preferencesKey = booleanPreferencesKey(key)
            ExampleApplication.exampleContext?.dataStore?.data?.map {
                it[preferencesKey] ?: (defaultValue ?: false)
            }?.first() ?: false
        } else {
            false
        }
    }

    private suspend fun putStringImpl(key: String?, value: String?) {
        if (key?.isNotEmpty() == true && value?.isNotEmpty() == true) {
            val preferencesKey = stringPreferencesKey(key)
            ExampleApplication.exampleContext?.run {
                dataStore.edit {
                    it[preferencesKey] = value
                }
            }
        }
    }

    private suspend fun getStringImpl(key: String?, defaultValue: String?): String? {
        return if (key?.isNotEmpty() == true) {
            val preferencesKey = stringPreferencesKey(key)
            ExampleApplication.exampleContext?.dataStore?.data?.map {
                it[preferencesKey] ?: (defaultValue ?: "")
            }?.first()
        } else {
            ""
        }
    }

    private suspend fun putStringSetImpl(key: String?, value: Set<String>?) {
        if (key?.isNotEmpty() == true && value?.isNotEmpty() == true) {
            val preferencesKey = stringSetPreferencesKey(key)
            ExampleApplication.exampleContext?.run {
                dataStore.edit {
                    it[preferencesKey] = value
                }
            }
        }
    }

    private suspend fun getStringSetImpl(key: String?, defaultValue: Set<String>?): Set<String> {
        return if (key?.isNotEmpty() == true) {
            val preferencesKey = stringSetPreferencesKey(key)
            ExampleApplication.exampleContext?.dataStore?.data?.map {
                it[preferencesKey] ?: (defaultValue ?: setOf())
            }?.first() ?: setOf()
        } else {
            setOf()
        }
    }
}