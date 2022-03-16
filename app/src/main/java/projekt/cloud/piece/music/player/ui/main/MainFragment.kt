package projekt.cloud.piece.music.player.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentMainBinding
import projekt.cloud.piece.music.player.ui.main.home.HomeFragment

class MainFragment: BaseFragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        with(binding.viewPager) {

            val fragments = listOf(
                HomeFragment()
            )

            adapter = object : FragmentStateAdapter(this@MainFragment) {
                override fun getItemCount() = fragments.size
                override fun createFragment(position: Int) = fragments[position]
            }

        }

    }

}