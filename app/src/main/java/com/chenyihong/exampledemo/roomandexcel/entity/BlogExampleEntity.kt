package com.chenyihong.exampledemo.roomandexcel.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chenyihong.exampledemo.roomandexcel.databse.TABLE_BLOG

@Entity(tableName = TABLE_BLOG)
data class BlogExampleEntity(
    @PrimaryKey
    val id: String,
    val cover: String,
    val title: String,
    val summary: String,
    val content: String
) {

    override fun toString(): String {
        return "BlogExampleEntity(id='$id', cover='$cover', title='$title', summary='$summary', content='$content')"
    }
}