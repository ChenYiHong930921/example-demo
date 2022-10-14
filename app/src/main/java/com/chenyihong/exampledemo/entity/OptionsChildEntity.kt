package com.chenyihong.exampledemo.entity

data class OptionsChildEntity(val testFunctionName: String, val testFunction: () -> Unit) {

    override fun toString(): String {
        return "OptionsChildEntity(testFunctionName=$testFunctionName, testFunction=$testFunction)"
    }
}