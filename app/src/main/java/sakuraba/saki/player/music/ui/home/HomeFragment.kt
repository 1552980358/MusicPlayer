package sakuraba.saki.player.music.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.FragmentHomeBinding

class HomeFragment: Fragment() {
    
    private lateinit var homeViewModel: HomeViewModel
    private var _fragmentHomeBinding: FragmentHomeBinding? = null
    private val fragmentHome get() = _fragmentHomeBinding!!
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)
        return fragmentHome.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentHomeBinding = null
    }
    
}