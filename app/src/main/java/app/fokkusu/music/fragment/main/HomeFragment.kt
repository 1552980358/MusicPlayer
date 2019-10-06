package app.fokkusu.music.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.fokkusu.music.R

/**
 * @File    : HomeFragment
 * @Author  : 1552980358
 * @Date    : 6 Oct 2019
 * @TIME    : 8:57 AM
 **/

class HomeFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}