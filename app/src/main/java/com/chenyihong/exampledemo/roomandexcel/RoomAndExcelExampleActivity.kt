package com.chenyihong.exampledemo.roomandexcel

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chenyihong.exampledemo.adapter.TextDataAdapter
import com.chenyihong.exampledemo.databinding.LayoutExampleRoomAndExcelActivityBinding
import com.chenyihong.exampledemo.roomandexcel.databse.AbstractExampleLocalDatabase
import com.chenyihong.exampledemo.roomandexcel.databse.ExampleDao
import com.chenyihong.exampledemo.roomandexcel.entity.BlogExampleEntity
import com.chenyihong.exampledemo.roomandexcel.entity.BlogTranslationExampleEntity
import jxl.Workbook
import jxl.write.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

class RoomAndExcelExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutExampleRoomAndExcelActivityBinding

    private val textContentAdapter = TextDataAdapter()

    private lateinit var exampleDao: ExampleDao

    private val blogAdapter = BlogAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutExampleRoomAndExcelActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        exampleDao = AbstractExampleLocalDatabase.getInstance(applicationContext).getExampleDao()

        binding.btnCreateExcel.setOnClickListener {
            writeGameDataToExcel()
        }
        binding.btnReadExcel.setOnClickListener {
            binding.rvBlogContainer.adapter = textContentAdapter
            readDataFromExcel()
        }
        binding.btnInsetBlogToDb.setOnClickListener {
            insertBlogToDb()
        }
        binding.btnGetAllBlog.setOnClickListener {
            binding.rvBlogContainer.adapter = blogAdapter
            getAllBlog()
        }
        binding.btnGetBlogById.setOnClickListener {
            binding.rvBlogContainer.adapter = blogAdapter
            getBlogById()
        }
    }

    private fun writeGameDataToExcel() {
        val mockDataList = ArrayList<ArrayList<String>>().apply {
            repeat(100) {
                val index = it + 1
                add(arrayListOf("$index", "示例博客$index 封面", "示例博客$index 标题", "示例博客$index 简介", "示例博客$index 文案"))
            }
        }
        File(if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) else filesDir, "translation_example.xls").let {
            try {
                it.deleteOnExit()
                it.createNewFile()
                Workbook.createWorkbook(it).apply {
                    createSheet("translation", 0).let { writableSheet ->
                        for ((rowIndex, data) in mockDataList.withIndex()) {
                            for ((columnIndex, content) in data.withIndex()) {
                                if (columnIndex != 0) {
                                    // 设置列宽
                                    writableSheet.setColumnView(columnIndex, 30)
                                }
                                writableSheet.addCell(Label(columnIndex, rowIndex, content))
                            }
                        }
                    }
                    write()
                    close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun readDataFromExcel() {
        assets.open("translation_example.xls").use {
            Workbook.getWorkbook(it).getSheet(0).apply {
                val blogTranslationList = ArrayList<String>()
                for (rowIndex in 0 until rows) {
                    val titleTranslation = getCell(3, rowIndex).contents
                    val summaryTranslation = getCell(5, rowIndex).contents
                    val contentTranslation = getCell(7, rowIndex).contents
                    if (titleTranslation.isNotEmpty() && summaryTranslation.isNotEmpty() && contentTranslation.isNotEmpty()) {
                        blogTranslationList.add("title:$titleTranslation, summary:$summaryTranslation, content:$contentTranslation")
                    }
                }
                textContentAdapter.setNewData(blogTranslationList)
            }
        }
    }

    private fun insertBlogToDb() {
        val blog = ArrayList<BlogExampleEntity>()
        val blogTranslation = ArrayList<BlogTranslationExampleEntity>()
        assets.open("translation_example.xls").use {
            Workbook.getWorkbook(it).getSheet(0).apply {
                for (rowIndex in 0 until rows) {
                    val id = getCell(0, rowIndex).contents
                    val cover = getCell(1, rowIndex).contents
                    val title = getCell(2, rowIndex).contents
                    val summary = getCell(4, rowIndex).contents
                    val content = getCell(6, rowIndex).contents
                    if (id.isNotEmpty() && cover.isNotEmpty() && title.isNotEmpty() && summary.isNotEmpty() && content.isNotEmpty()) {
                        blog.add(BlogExampleEntity(id, cover, title, summary, content))
                    }

                    val titleTranslation = getCell(3, rowIndex).contents
                    val summaryTranslation = getCell(5, rowIndex).contents
                    val contentTranslation = getCell(7, rowIndex).contents
                    if (id.isNotEmpty() && titleTranslation.isNotEmpty() && summaryTranslation.isNotEmpty() && contentTranslation.isNotEmpty()) {
                        blogTranslation.add(BlogTranslationExampleEntity(id, Locale.ENGLISH.language, titleTranslation, summaryTranslation, contentTranslation))
                    }
                }
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            exampleDao.insertBlogExample(blog)
            exampleDao.insertBlogExampleTranslation(blogTranslation)
        }
    }

    private fun getAllBlog() {
        lifecycleScope.launch(Dispatchers.IO) {
            exampleDao.queryAllBlogWithLanguage(Locale.getDefault().language).let {
                withContext(Dispatchers.Main) {
                    blogAdapter.setNewData(it)
                }
            }
        }
    }

    private fun getBlogById() {
        lifecycleScope.launch(Dispatchers.IO) {
            exampleDao.queryBlogWitIdAndLanguage("1", Locale.getDefault().language)?.let {
                withContext(Dispatchers.Main) {
                    blogAdapter.setNewData(arrayListOf(it))
                }
            }
        }
    }
}