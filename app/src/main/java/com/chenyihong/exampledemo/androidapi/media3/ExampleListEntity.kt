package com.chenyihong.exampledemo.androidapi.media3

data class ExampleListEntity(
    // 1为video item,2为普通item
    val type: Int,
    val videoUrl: String?,
    val itemText: String?
) {

    override fun toString(): String {
        return "ExampleListEntity(type=$type, videoUrl=$videoUrl, itemText=$itemText)"
    }
}