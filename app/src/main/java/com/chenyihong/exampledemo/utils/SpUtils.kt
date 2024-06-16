package com.chenyihong.exampledemo.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.chenyihong.exampledemo.R

object SpUtils {

    private var sharedPreferences: SharedPreferences? = null

    @JvmStatic
    @JvmOverloads
    fun init(context: Context, spName: String? = null) {
        if (sharedPreferences == null) {
            val finalSpName = if (TextUtils.isEmpty(spName)) {
                context.getString(R.string.app_name)
            } else {
                spName
            }
            sharedPreferences = context.getSharedPreferences(finalSpName, Context.MODE_PRIVATE)
        }
    }

    @JvmStatic
    fun isInit(): Boolean {
        return sharedPreferences != null
    }

    /**
     * SP中写入String
     *
     * @param key   键
     * @param value 值
     */
    @JvmStatic
    fun put(key: String, value: String) {
        sharedPreferences?.run {
            edit().putString(key, value).apply()
        }
    }

    /**
     * SP中读取String
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`""`
     */
    @JvmStatic
    fun getString(key: String): String? {
        return getString(key, "")
    }

    /**
     * SP中读取String
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    @JvmStatic
    fun getString(key: String, defaultValue: String): String? {
        return sharedPreferences?.getString(key, defaultValue)
    }

    /**
     * SP中写入int
     *
     * @param key   键
     * @param value 值
     */
    @JvmStatic
    fun put(key: String, value: Int) {
        sharedPreferences?.run {
            edit().putInt(key, value).apply()
        }
    }

    /**
     * SP中读取int
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    @JvmStatic
    fun getInt(key: String): Int {
        return getInt(key, -1)
    }

    /**
     * SP中读取int
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    @JvmStatic
    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences?.getInt(key, defaultValue) ?: -1
    }

    /**
     * SP中写入long
     *
     * @param key   键
     * @param value 值
     */
    @JvmStatic
    fun put(key: String, value: Long) {
        sharedPreferences?.run {
            edit().putLong(key, value).apply()
        }
    }

    /**
     * SP中读取long
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    @JvmStatic
    fun getLong(key: String): Long {
        return getLong(key, -1L)
    }

    /**
     * SP中读取long
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    @JvmStatic
    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences?.getLong(key, defaultValue) ?: -1L
    }

    /**
     * SP中写入float
     *
     * @param key   键
     * @param value 值
     */
    @JvmStatic
    fun put(key: String, value: Float) {
        sharedPreferences?.run {
            edit().putFloat(key, value).apply()
        }
    }

    /**
     * SP中读取float
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    @JvmStatic
    fun getFloat(key: String): Float {
        return getFloat(key, -1f)
    }

    /**
     * SP中读取float
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    @JvmStatic
    fun getFloat(key: String, defaultValue: Float): Float {
        return sharedPreferences?.getFloat(key, defaultValue) ?: -1f
    }

    /**
     * SP中写入boolean
     *
     * @param key   键
     * @param value 值
     */
    @JvmStatic
    fun put(key: String, value: Boolean) {
        sharedPreferences?.run {
            edit().putBoolean(key, value).apply()
        }
    }

    /**
     * SP中读取boolean
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`false`
     */
    @JvmStatic
    fun getBoolean(key: String): Boolean {
        return getBoolean(key, false)
    }

    /**
     * SP中读取boolean
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    @JvmStatic
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences?.getBoolean(key, defaultValue) ?: false
    }

    /**
     * SP中写入String集合
     *
     * @param key    键
     * @param values 值
     */
    @JvmStatic
    fun put(key: String, values: Set<String?>) {
        sharedPreferences?.run {
            edit().putStringSet(key, values).apply()
        }
    }

    /**
     * SP中读取StringSet
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`Collections.<String>emptySet()`
     */
    @JvmStatic
    fun getStringSet(key: String): Set<String>? {
        return getStringSet(key, emptySet<String>())
    }

    /**
     * SP中读取StringSet
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    @JvmStatic
    fun getStringSet(key: String, defaultValue: Set<String?>): Set<String>? {
        return sharedPreferences?.getStringSet(key, defaultValue)
    }

    /**
     * SP中获取所有键值对
     *
     * @return Map对象
     */
    @JvmStatic
    val all: Map<String, *>?
        get() = sharedPreferences?.all

    /**
     * SP中是否存在该key
     *
     * @param key 键
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    @JvmStatic
    fun contains(key: String): Boolean {
        return sharedPreferences?.contains(key) ?: false
    }

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    @JvmStatic
    fun remove(key: String) {
        sharedPreferences?.run {
            edit().remove(key).apply()
        }
    }

    /**
     * SP中清除所有数据
     */
    @JvmStatic
    fun clear() {
        sharedPreferences?.run {
            edit().clear().apply()
        }
    }
}