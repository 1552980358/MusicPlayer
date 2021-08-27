package sakuraba.saki.player.music

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.Menu
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import sakuraba.saki.player.music.databinding.ActivityMainBinding
import sakuraba.saki.player.music.service.PlayService
import sakuraba.saki.player.music.service.PlayService.Companion.ROOT_ID
import sakuraba.saki.player.music.ui.home.HomeFragment.Companion.INTENT_ACTIVITY_FRAGMENT_INTERFACE
import sakuraba.saki.player.music.util.ActivityFragmentInterface
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_LIST
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_POS

class MainActivity: AppCompatActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    
    private var _activityMainMainBinding: ActivityMainBinding? = null
    private val activityMain get() = _activityMainMainBinding!!
    
    private lateinit var mediaBrowserCompat: MediaBrowserCompat
    private lateinit var connectionCallback: MediaBrowserCompat.ConnectionCallback
    private lateinit var subscriptionCallback: MediaBrowserCompat.SubscriptionCallback
    
    private lateinit var mediaControllerCompat: MediaControllerCompat
    private lateinit var mediaControllerCallback: MediaControllerCompat.Callback
    
    private lateinit var activityFragmentInterface: ActivityFragmentInterface
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        activityFragmentInterface = ActivityFragmentInterface { pos, audioInfo, audioInfoList ->
            mediaControllerCompat.transportControls.playFromMediaId(audioInfo?.audioId, bundle {
                putInt(EXTRAS_AUDIO_INFO_POS, pos)
                putSerializable(EXTRAS_AUDIO_INFO, audioInfo)
                putSerializable(EXTRAS_AUDIO_INFO_LIST, audioInfoList)
            })
        }
    
        intent?.putExtra(INTENT_ACTIVITY_FRAGMENT_INTERFACE, activityFragmentInterface)
        
        _activityMainMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMain.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home), activityMain.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        activityMain.navView.setupWithNavController(navController)
        
        mediaControllerCallback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            }
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            }
        }
        
        connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                if (mediaBrowserCompat.isConnected) {
                    mediaBrowserCompat.unsubscribe(ROOT_ID)
                    mediaBrowserCompat.subscribe(ROOT_ID, subscriptionCallback)
                    
                    mediaControllerCompat = MediaControllerCompat(this@MainActivity, mediaBrowserCompat.sessionToken)
                    MediaControllerCompat.setMediaController(this@MainActivity, mediaControllerCompat)
                    mediaControllerCompat.registerCallback(mediaControllerCallback)
                }
            }
            override fun onConnectionSuspended() {
            }
            override fun onConnectionFailed() {
            }
        }
        
        subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() { }
        mediaBrowserCompat = MediaBrowserCompat(this, ComponentName(this, PlayService::class.java), connectionCallback, null)
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    
    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    
    override fun onDestroy() {
        super.onDestroy()
        _activityMainMainBinding = null
    }
}