package com.minigame.testapp.ui.entity

import com.chenyihong.exampledemo.entity.OptionsChildEntity

data class OptionsEntity(val moduleName: String, var expanded: Boolean = false, val containerTest: ArrayList<OptionsChildEntity>) {}