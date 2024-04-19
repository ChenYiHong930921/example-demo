package com.chenyihong.exampledemo.roomandexcel.databse

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chenyihong.exampledemo.roomandexcel.entity.BlogExampleEntity
import com.chenyihong.exampledemo.roomandexcel.entity.BlogTranslationExampleEntity

@Database(entities = [
    BlogExampleEntity::class,
    BlogTranslationExampleEntity::class
], version = 1, exportSchema = false)
abstract class AbstractExampleLocalDatabase : RoomDatabase() {

    companion object {
        @Volatile
        var exampleLocalDataBase: AbstractExampleLocalDatabase? = null

        fun getInstance(context: Context): AbstractExampleLocalDatabase {
            if (exampleLocalDataBase == null) {
                synchronized(AbstractExampleLocalDatabase::class) {
                    if (exampleLocalDataBase == null) {
                        exampleLocalDataBase = buildDatabase(context)
                    }
                }
            }
            return exampleLocalDataBase!!
        }

        private fun buildDatabase(context: Context): AbstractExampleLocalDatabase {
            return Room.databaseBuilder(context, AbstractExampleLocalDatabase::class.java, "ExampleRoomDb")
                // 从assets中的预装数据库创建当前数据库
                // 预装数据库没准备好前可以先注释
                .createFromAsset("ExampleDatabase.db")
                .build()
        }
    }

    abstract fun getExampleDao(): ExampleDao
}