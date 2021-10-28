package sakuraba.saki.player.music

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import sakuraba.saki.player.music.databinding.ActivityAudioDetailBinding
import sakuraba.saki.player.music.util.ActivityUtil.translateExit

class AudioDetailActivity: AppCompatActivity() {

    private var _activityAudioDetailBinding: ActivityAudioDetailBinding? = null
    private val activityAudioDetail get() = _activityAudioDetailBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityAudioDetailBinding = ActivityAudioDetailBinding.inflate(layoutInflater)
        setContentView(activityAudioDetail.root)
        setSupportActionBar(activityAudioDetail.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activityAudioDetail.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (findNavController(R.id.fragment).currentDestination?.id == R.id.nav_audio_detail) {
            // fadeAnim()
            translateExit()
        }
    }

}