package com.davidmiguel.multistateswitch.sample.viewpager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.davidmiguel.multistateswitch.sample.R
import com.davidmiguel.multistateswitch.sample.databinding.ActivityViewPagerBinding
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerActivity : AppCompatActivity() {

    lateinit var binding: ActivityViewPagerBinding
    private val adapter = ViewPagerAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_pager)
        setupDefaultSwitch()
    }

    private fun setupDefaultSwitch() {
        binding.pager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = "Page $position"
        }.attach()
    }
}
