package com.davidmiguel.multistateswitch.sample

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.davidmiguel.multistateswitch.State
import com.davidmiguel.multistateswitch.StateListener
import com.davidmiguel.multistateswitch.StateStyle
import com.davidmiguel.multistateswitch.sample.databinding.ActivityMainBinding
import com.davidmiguel.multistateswitch.sample.viewpager.ViewPagerActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity(), StateListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupDefaultSwitch()
        setupDisabledSwitch()
        setupCustomizedSwitch()
        setupCustomizedRadiusSwitch()
        setupAddRemoveSwitch()
        setupViewPagerBtn()
    }

    override fun onStateSelected(stateIndex: Int, state: State) {
        Log.d("onStateSelected", getString(R.string.listener, stateIndex, state.text))
        binding.listener.text = getString(R.string.listener, stateIndex, state.text)
    }

    private fun setupDefaultSwitch() {
        binding.defaultSwitch.addStatesFromStrings(listOf("One", "Two", "Three"))
        binding.defaultSwitch.addStateListener(this)
    }

    private fun setupDisabledSwitch() {
        binding.disabledSwitch.addStatesFromStrings(listOf("€0", "€25", "€50", "€75", "€100"))
        binding.disabledSwitch.addStateListener(this)
    }

    private fun setupCustomizedSwitch() {
        binding.customizedSwitch.addStateFromString(
            "Cold",
            StateStyle.Builder()
                .withSelectedBackgroundColor(Color.BLUE)
                .build()
        )
        binding.customizedSwitch.addState(
            State(
                text = "ON",
                selectedText = "OFF",
                disabledText = "OFF"
            ),
            StateStyle.Builder()
                .withTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .withDisabledBackgroundColor(Color.BLACK)
                .withDisabledTextColor(Color.WHITE)
                .build()
        )
        binding.customizedSwitch.addStateFromString(
            "Hot", StateStyle.Builder()
                .withSelectedBackgroundColor(Color.RED)
                .build()
        )
        binding.customizedSwitch.addStateListener(this)
        setupCustomizedButtons()
    }

    private fun setupCustomizedRadiusSwitch() {
        binding.radiusSwitch.addStatesFromStrings(listOf("Off", "x2", "x3", "x4", "x5"))
        binding.radiusSwitch.addStateListener(this)
    }

    private fun setupCustomizedButtons() {
        binding.select1Btn.setOnClickListener { binding.customizedSwitch.selectState(0) }
        binding.select2Btn.setOnClickListener { binding.customizedSwitch.selectState(1) }
        binding.select3Btn.setOnClickListener { binding.customizedSwitch.selectState(2) }
    }

    private fun setupAddRemoveSwitch() {
        binding.addRemoveSwitch.addStateListener(this)
        binding.addBtn.setOnClickListener {
            binding.addRemoveSwitch.addStateFromString("R${Random.nextInt(100)}")
        }
        binding.removeBtn.setOnClickListener {
            val numStates = binding.addRemoveSwitch.getNumberStates()
            if (numStates > 0) {
                binding.addRemoveSwitch.removeState(numStates - 1)
            }
        }
    }

    private fun setupViewPagerBtn() {
        binding.viewPagerBtn.setOnClickListener {
            startActivity(Intent(this, ViewPagerActivity::class.java))
        }
    }
}