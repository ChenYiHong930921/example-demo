package com.chenyihong.exampledemo.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonEntity(
    val name: String,
    val age: Int,
    val gender: Int,
    val weight: Float,
    val height: Float
) : Parcelable {

    override fun toString(): String {
        return "PersonEntity(name='$name', age=$age, gender=$gender, weight=$weight, height=$height)"
    }
}