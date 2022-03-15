package projekt.cloud.piece.music.player.ui.main

import android.content.res.ColorStateList
import android.content.res.ColorStateList.valueOf
import android.graphics.Color.WHITE
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.android.material.transition.Hold
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseMainFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentMainBinding
import projekt.cloud.piece.music.player.ui.main.home.HomeFragment

class MainFragment: BaseMainFragment(), OnNavigationItemSelectedListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val appBarFragmentMain get() = binding.appBarFragmentMain
    private val extendedFloatingActionButton get() = appBarFragmentMain.extendedFloatingActionButton

    private lateinit var navController: NavController

    private val albumBitmap40DpMap get() = activityInterface.albumBitmap40DpMap
    private val audioBitmap40DpMap get() = activityInterface.audioBitmap40DpMap

    private val fragmentList = listOf(
        HomeFragment()
    )

    private var countDownJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        navController = findNavController()

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.statusBarColor, typedValue, true)
        requireActivity().window.statusBarColor = typedValue.data
        requireActivity().window.navigationBarColor = WHITE

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.appBarFragmentMain.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            with(ActionBarDrawerToggle(this,
                binding.root,
                binding.appBarFragmentMain.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)) {
                binding.root.addDrawerListener(this)
                syncState()
            }
        }
        binding.navView.setNavigationItemSelectedListener(this)

        activityInterface.setUpMain {
            if (extendedFloatingActionButton.isExtended) {
                extendedFloatingActionButton.shrink()
            }
            countDownJob?.cancel()
            loadAudioItem(it)
        }

        with(extendedFloatingActionButton) {
            setAnimateShowBeforeLayout(true)
            when (val audioItem = activityInterface.audioItem) {
                null -> hide()
                else -> loadAudioItem(audioItem)
            }

            setOnClickListener {
                navController.navigate(R.id.action_nav_main_to_nav_play, null, null,
                    FragmentNavigatorExtras(binding.appBarFragmentMain.extendedFloatingActionButton to "transition_fab")
                )
            }
        }

        return binding.root
    }

    private fun loadAudioItem(audioItem: AudioItem) {
        countDownJob = io {
            val imageDrawable = BitmapDrawable(resources, audioBitmap40DpMap[audioItem.id]
                ?: albumBitmap40DpMap[audioItem.album]
                ?: activityInterface.defaultAudioImage
            )

            activityInterface.audioDatabase.color.query(audioItem.id, audioItem.album).apply {

                if (!extendedFloatingActionButton.isShown) {
                    ui { extendedFloatingActionButton.show() }
                }

                with(extendedFloatingActionButton) {
                    ui {
                        icon = imageDrawable
                        backgroundTintList = valueOf(backgroundColor)
                        setTextColor(primaryColor)
                        if (text != audioItem.title) {
                            text = audioItem.title
                            extend()
                        }
                    }
                }
            }
            
            delay(5000L)
            ui { extendedFloatingActionButton.shrink() }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        with(binding.appBarFragmentMain.viewPager) {

            /**
             * For ViewPager2, method of disabling over scroll is token from
             * https://stackoverflow.com/a/56942231/11685230
             **/
            getChildAt(0).overScrollMode = OVER_SCROLL_NEVER

            adapter = object : FragmentStateAdapter(this@MainFragment) {
                override fun getItemCount() = fragmentList.size
                override fun createFragment(position: Int) = fragmentList[position]
            }

        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

}