package sakuraba.saki.player.music

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import sakuraba.saki.player.music.databinding.ActivityAudioDetailBinding

class AudioDetailActivity: AppCompatActivity() {

    private var _activityAudioDetailBinding: ActivityAudioDetailBinding? = null
    private val activityAudioDetail get() = _activityAudioDetailBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityAudioDetailBinding = ActivityAudioDetailBinding.inflate(layoutInflater)
        setContentView(activityAudioDetail.root)
        setSupportActionBar(activityAudioDetail.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activityAudioDetail.toolbar.setNavigationOnClickListener { finish() }
    }

}