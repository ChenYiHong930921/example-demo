package com.chenyihong.exampledemo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2Adapter : FragmentStateAdapter {

    private val fragments: ArrayList<Class<out Fragment?>>

    constructor(fragment: Fragment, fragments: ArrayList<Class<out Fragment?>>) : super(fragment) {
        this.fragments = fragments
    }

    constructor(fragmentActivity: FragmentActivity, fragments: ArrayList<Class<out Fragment?>>) : super(fragmentActivity) {
        this.fragments = fragments
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position].getDeclaredConstructor().newInstance()!!
    }
}