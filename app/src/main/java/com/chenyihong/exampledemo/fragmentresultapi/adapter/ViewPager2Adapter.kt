package com.chenyihong.exampledemo.fragmentresultapi.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chenyihong.exampledemo.fragmentresultapi.DialogFragment


class ViewPager2Adapter(fragment: Fragment, private val fragments: ArrayList<Class<out Fragment?>>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position].newInstance()!!
    }
}