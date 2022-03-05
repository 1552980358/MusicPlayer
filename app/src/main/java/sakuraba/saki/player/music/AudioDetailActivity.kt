package sakuraba.saki.player.music

import android.app.Instrumentation
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import sakuraba.saki.player.music.databinding.ActivityAudioDetailBinding
import sakuraba.saki.player.music.ui.audioDetail.AudioDetailFragment
import sakuraba.saki.player.music.ui.audioDetail.lyricView.LyricViewFragment

class AudioDetailActivity: BaseThemeAppCompatActivity() {

    private var _activityAudioDetailBinding: ActivityAudioDetailBinding? = null
    private val activityAudioDetail get() = _activityAudioDetailBinding!!
    
    private var needBackPressedAnim = false

    private lateinit var navController: NavController

    private val navHostFragment get() = activityAudioDetail.fragmentContainerView.getFragment<NavHostFragment>()

    private val fragmentLifecycleCallbacks =  object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
            Log.e(f::class.java.simpleName, "onFragmentAttached")
        }
        override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            Log.e(f::class.java.simpleName, "onFragmentCreated")
        }
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            Log.e(f::class.java.simpleName, "onFragmentViewCreated")
        }
        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentStarted")
        }
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentResumed")
        }
        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentPaused")
        }
        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentStopped")
        }
        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentViewDestroyed")
        }
        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentDestroyed")
        }
        override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentDetached")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
        _activityAudioDetailBinding = ActivityAudioDetailBinding.inflate(layoutInflater)
        setContentView(activityAudioDetail.root)
        setSupportActionBar(activityAudioDetail.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activityAudioDetail.toolbar.setNavigationOnClickListener { onBackPressed() }
        needBackPressedAnim = intent?.hasExtra(PlayActivity::class.java.simpleName) == true
        if (needBackPressedAnim) {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_up)
        }
        navController = navHostFragment.navController
    }

    override fun onStop() {
        Instrumentation().callActivityOnSaveInstanceState(this, Bundle())
        super.onStop()
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.nav_audio_detail -> {
                when {
                    needBackPressedAnim -> {
                        super.onBackPressed()
                        overridePendingTransition(R.anim.translate_up_pop_enter, R.anim.translate_up_pop_exit)
                    }
                    else -> {
                        (navHostFragment.childFragmentManager.fragments.first() as AudioDetailFragment?)
                            ?.onActivityBackPressed()
                    }
                }
            }
            R.id.nav_lyric_view -> {
                (navHostFragment.childFragmentManager.fragments.first() as LyricViewFragment?)
                        ?.onBackPressed()
                super.onBackPressed()
            }
            else -> super.onBackPressed()
        }
    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        super.onDestroy()
    }

}