package com.chenyihong.exampledemo.customview.itemdecoration

data class ItemDecorationExampleDataEntity(
    var id: Int,
    var name: String?,
    var icon: Int = -1,
    var selected: Boolean = false
) {

    override fun toString(): String {
        return "ItemDecorationExampleDataEntity(id=$id, name=$name, icon=$icon, selected=$selected)"
    }
}