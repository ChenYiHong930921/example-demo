package com.chenyihong.exampledemo.roomandexcel.databse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chenyihong.exampledemo.roomandexcel.entity.BlogExampleEntity
import com.chenyihong.exampledemo.roomandexcel.entity.BlogTranslationExampleEntity

const val TABLE_BLOG = "blog"
const val TABLE_BLOG_TRANSLATION = "blog_translation"

@Dao
interface ExampleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlogExample(game: List<BlogExampleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlogExampleTranslation(gameTranslation: List<BlogTranslationExampleEntity>)

    /**
     * 结合两个表进行查询
     * 当前语言有翻译文案时使用翻译文案
     * 没有时使用默认文案
     */
    @Query("SELECT blog.id AS id, " +
            "blog.cover AS cover, " +
            "COALESCE(blogTranslation.title, blog.title) AS title, " +
            "COALESCE(blogTranslation.summary, blog.summary) AS summary, " +
            "COALESCE(blogTranslation.content, blog.content) AS content " +
            "FROM $TABLE_BLOG blog " +
            "LEFT JOIN $TABLE_BLOG_TRANSLATION blogTranslation ON blog.id = blogTranslation.id AND blogTranslation.language = :currentLanguage")
    suspend fun queryAllBlogWithLanguage(currentLanguage: String): List<BlogExampleEntity>

    /**
     * 结合两个表进行查询
     * 当前语言有翻译文案时使用翻译文案
     * 没有时使用默认文案
     */
    @Query("SELECT blog.id AS id, " +
            "blog.cover AS cover, " +
            "COALESCE(blogTranslation.title, blog.title) AS title, " +
            "COALESCE(blogTranslation.summary, blog.summary) AS summary, " +
            "COALESCE(blogTranslation.content, blog.content) AS content " +
            "FROM $TABLE_BLOG blog " +
            "LEFT JOIN $TABLE_BLOG_TRANSLATION blogTranslation ON blog.id = blogTranslation.id AND blogTranslation.language = :currentLanguage WHERE blog.id = :id")
    suspend fun queryBlogWitIdAndLanguage(id: String, currentLanguage: String): BlogExampleEntity?
}