package com.chenyihong.exampledemo.roomandexcel.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chenyihong.exampledemo.roomandexcel.databse.TABLE_BLOG_TRANSLATION

@Entity(tableName = TABLE_BLOG_TRANSLATION)
data class BlogTranslationExampleEntity(
    @PrimaryKey
    val id: String,
    val language: String,
    val title: String,
    val summary: String,
    val content :String
) {

    override fun toString(): String {
        return "BlogTranslationExampleEntity(id='$id', language='$language', title='$title', summary='$summary', content='$content')"
    }
}