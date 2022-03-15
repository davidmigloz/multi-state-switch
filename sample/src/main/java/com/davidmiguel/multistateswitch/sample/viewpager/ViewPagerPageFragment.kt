package com.davidmiguel.multistateswitch.sample.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.davidmiguel.multistateswitch.sample.R
import com.davidmiguel.multistateswitch.sample.databinding.FragmentViewPagerPageBinding

const val ARG_PAGE = "ViewPagerPageFragment.ARG_PAGE"

class ViewPagerPageFragment : Fragment() {

    private lateinit var binding: FragmentViewPagerPageBinding
    private var currentPage: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewPagerPageBinding.inflate(inflater, container, false)
        currentPage = arguments?.getInt(ARG_PAGE) ?: 0
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupDefaultSwitch()
    }

    private fun setupDefaultSwitch() {
        binding.defaultSwitch.addStatesFromStrings(List(TOTAL_PAGES) { "Page $it" })
        binding.defaultSwitch.addStateListener { stateIndex, state ->
            binding.listener.text = getString(R.string.listener, stateIndex, state.text)
        }
        binding.defaultSwitch.post {
            binding.defaultSwitch.selectState(currentPage)
        }
    }
}
