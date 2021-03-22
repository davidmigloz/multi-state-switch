package com.davidmiguel.multistateswitch.sample.viewpager

import android.os.Bundle
import androidx.viewpager2.adapter.FragmentStateAdapter

const val TOTAL_PAGES = 3

class ViewPagerAdapter(activity: ViewPagerActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = TOTAL_PAGES

    override fun createFragment(position: Int) = ViewPagerPageFragment().apply {
        arguments = Bundle().apply { putInt(ARG_PAGE, position) }
    }
}