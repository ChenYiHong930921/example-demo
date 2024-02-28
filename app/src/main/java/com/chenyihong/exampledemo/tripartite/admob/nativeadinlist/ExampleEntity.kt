package com.chenyihong.exampledemo.tripartite.admob.nativeadinlist

const val LAYOUT_TYPE_NORMAL = 0
const val LAYOUT_TYPE_AD = 1

data class ExampleEntity(
    val layoutType: Int,
    val exampleContent: String
) {

    override fun toString(): String {
        return "ExampleEntity(layoutType=$layoutType, exampleContent='$exampleContent')"
    }
}