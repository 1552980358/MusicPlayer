package projekt.cloud.piece.music.player.ui.play.playControl

import android.animation.ValueAnimator.ofArgb
import android.bluetooth.BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED
import android.content.Context.AUDIO_SERVICE
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.drawable.RippleDrawable
import android.media.AudioManager
import android.media.AudioManager.ACTION_HEADSET_PLUG
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import lib.github1552980358.ktExtension.android.content.broadcastReceiver
import lib.github1552980358.ktExtension.android.content.getStatusBarHeight
import lib.github1552980358.ktExtension.android.content.register
import lib.github1552980358.ktExtension.android.view.heightF
import lib.github1552980358.ktExtension.android.view.widthF
import lib.github1552980358.ktExtension.androidx.fragment.app.getDrawable
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.database.item.PlaylistContentItem
import projekt.cloud.piece.music.player.databinding.FragmentPlayControlBinding
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT_ONE
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_SHUFFLE
import projekt.cloud.piece.music.player.service.play.Config.getConfig
import projekt.cloud.piece.music.player.service.play.Config.setConfig
import projekt.cloud.piece.music.player.ui.play.PlayFragment
import projekt.cloud.piece.music.player.ui.play.PlayViewModel
import projekt.cloud.piece.music.player.ui.play.playControl.util.RecyclerViewAdapterUtil
import projekt.cloud.piece.music.player.util.ActivityUtil.pixelHeight
import projekt.cloud.piece.music.player.util.AudioUtil.deviceDrawableId
import projekt.cloud.piece.music.player.util.ColorUtil.isLight
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION
import projekt.cloud.piece.music.player.util.Constant.PLAYLIST_LIKES
import projekt.cloud.piece.music.player.util.ContextUtil.navigationBarHeight

class PlayControlFragment: BaseFragment() {

    companion object {
        private const val TAG = "PlayControlFragment"
        private const val BOTTOM_SHEET_SHOW_DELAY = 300L
    }

    private var _binding: FragmentPlayControlBinding? = null
    private val binding get() = _binding!!
    private val contentControl get() = binding.contentControlPlayControl
    private val bottom get() = binding.contentBottomPlayControl
    private val imageViewCycle get() = contentControl.imageViewCycle
    private val imageViewPrev get() = contentControl.imageViewPrev
    private val imageViewNext get() = contentControl.imageViewNext
    private val imageViewShuffle get() = contentControl.imageViewShuffle
    private val recyclerView get() = bottom.recyclerView

    private val database get() = activityViewModel.database
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private val transportControls get() = activityViewModel.mediaControllerCompat.transportControls

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    private lateinit var audioManager: AudioManager

    private var heartItem: MenuItem? = null
    private var isLiked = false

    private val broadcastReceiver = broadcastReceiver { _, intent, _ ->
        when (intent?.action) {
            ACTION_HEADSET_PLUG, ACTION_CONNECTION_STATE_CHANGED -> bottom.device = getDrawable(audioManager.deviceDrawableId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioManager = requireContext().getSystemService(AUDIO_SERVICE) as AudioManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play_control, container, false)
        binding.imageViewImage.apply {
            layoutParams = layoutParams.apply { height = resources.displayMetrics.widthPixels }
        }
        val bottomHeight = requireActivity().pixelHeight - resources.displayMetrics.widthPixels
        contentControl.linearLayoutWrapper.apply {
            layoutParams = layoutParams.apply { height = bottomHeight * 2 / 5 }
        }
        bottomSheetBehavior = BottomSheetBehavior.from(bottom.root)
        bottomSheetBehavior.peekHeight = bottomHeight * 3 / 5
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = STATE_HIDDEN
        bottom.root.apply {
            layoutParams = layoutParams.apply {
                height = pixelHeight - requireContext().getStatusBarHeight()
            }
            setContentPadding(0, 0, 0, navigationBarHeight)
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        contentControl.floatingActionButton.setOnClickListener {
            when {
                activityViewModel.isPlaying -> transportControls.pause()
                else -> transportControls.play()
            }
        }

        bottom.device = getDrawable(audioManager.deviceDrawableId)
        broadcastReceiver.register(requireContext(), ACTION_HEADSET_PLUG, ACTION_CONNECTION_STATE_CHANGED)

        contentControl.seekbar.setOnSeekChangeListener { progress, isReleased ->
            if (contentControl.isSeekbarTouched != !isReleased) {
                contentControl.isSeekbarTouched = !isReleased
            }
            when {
                !isReleased -> contentControl.seekbarTouchedProgress = progress
                else -> activityViewModel.mediaControllerCompat.transportControls.seekTo(progress)
            }
        }

        @Suppress("ClickableViewAccessibility")
        with(contentControl.root) {
            setOnTouchListener { _, motionEvent ->

                when (motionEvent.action) {
                    ACTION_DOWN -> {
                        (background as RippleDrawable).setHotspot(motionEvent.x, motionEvent.y)
                        isPressed = true
                    }
                    ACTION_CANCEL -> isPressed = false
                    ACTION_UP -> {
                        isPressed = false
                        val rawX = motionEvent.rawX
                        val rawY = motionEvent.rawY
                        val rawAxis = IntArray(2)
                        when {

                            compareAxis(rawX, rawY, imageViewCycle, rawAxis) -> {
                                var playConfig = activityViewModel.playConfig
                                val repeat = playConfig.getConfig(PLAY_CONFIG_REPEAT)
                                val repeatOne = playConfig.getConfig(PLAY_CONFIG_REPEAT_ONE)
                                when {
                                    repeat && !repeatOne ->
                                        playConfig = playConfig.setConfig(PLAY_CONFIG_REPEAT, false)
                                            .setConfig(PLAY_CONFIG_REPEAT_ONE, true)
                                    !repeat && repeatOne ->
                                        playConfig = playConfig.setConfig(PLAY_CONFIG_REPEAT_ONE, false)
                                    !repeat && !repeatOne ->
                                        playConfig = playConfig.setConfig(PLAY_CONFIG_REPEAT, true)
                                }
                                activityViewModel.updatePlayConfig(playConfig)
                            }

                            compareAxis(rawX, rawY, imageViewPrev, rawAxis) ->
                                transportControls.skipToPrevious()

                            compareAxis(rawX, rawY, imageViewNext, rawAxis) ->
                                transportControls.skipToNext()

                            compareAxis(rawX, rawY, imageViewShuffle, rawAxis) -> {
                                recyclerViewAdapterUtil.hasShuffled = true
                                activityViewModel.updatePlayConfig(
                                    activityViewModel.playConfig.run { setConfig(PLAY_CONFIG_SHUFFLE, !getConfig(PLAY_CONFIG_SHUFFLE)) }
                                )
                            }

                        }
                    }
                }

                true
            }
        }

        setObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_fragment_play_control, menu)
        heartItem = menu.getItem(0).apply {
            updateHeatItemIO(activityViewModel.audioItem, this)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_like -> {
                io {
                    activityViewModel.audioItem?.let {
                        isLiked = when (val playlistContentItem = database.playlistContent.queryLike(it.id)) {
                            null -> {
                                database.playlistContent.insert(PlaylistContentItem(audio = it.id, playlist = PLAYLIST_LIKES))
                                true
                            }
                            else -> {
                                database.playlistContent.delete(playlistContentItem)
                                false
                            }
                        }
                        item.setIsLiked(isLiked)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed(): Boolean {
        if (bottomSheetBehavior.state != STATE_COLLAPSED) {
            bottomSheetBehavior.state = STATE_COLLAPSED
            return false
        }
        return super.onBackPressed()
    }

    private fun setObservers() = io {
        ui {
            recyclerViewAdapterUtil = RecyclerViewAdapterUtil(recyclerView) {
                activityViewModel.playList?.let { playlist ->
                    activityViewModel.mediaControllerCompat.transportControls.skipToQueueItem(playlist[it].index.toLong())
                }
            }
        }
        activityViewModel.setAudioItemObserver(TAG) { audioItem -> updateAudioItem(audioItem) }
        activityViewModel.setCoverArtBitmapObserver(TAG) { bitmap -> binding.imageBitmap = bitmap }
        activityViewModel.setPlayStateObserver(TAG) { isPlaying ->
            if (contentControl.isPlaying != isPlaying) {
                contentControl.isPlaying = isPlaying
            }
        }
        activityViewModel.setProgressObservers(TAG) { progress -> contentControl.progress = progress }
        activityViewModel.setPlayConfigObserver(TAG) { playConfig -> contentControl.playConfig = playConfig }
        activityViewModel.setPlaylistObserver(TAG) { playlist -> recyclerViewAdapterUtil.playlist = playlist }
        activityViewModel.getPlaylistSync()
    }

    private fun delayShowBottomSheet() = io {
        delay(BOTTOM_SHEET_SHOW_DELAY)
        ui {
            bottomSheetBehavior.state = STATE_COLLAPSED
            bottomSheetBehavior.isHideable = false
        }
    }

    private fun updateAudioItem(audioItem: AudioItem) = io {
        ui {
            bottom.audioItem = audioItem
            contentControl.duration = audioItem.duration
        }
        database.color.query(audioItem.id, audioItem.album).apply {
            updateColor(backgroundColor, primaryColor, secondaryColor)
            with(backgroundColor.isLight) {
                when (contentControl.iconTintColor) {
                    null -> contentControl.iconTintColor = if (this) BLACK else WHITE
                    else -> when {
                        this -> if (contentControl.iconTintColor != BLACK) {
                            ofArgb(WHITE, BLACK).apply {
                                duration = ANIMATION_DURATION
                                addUpdateListener { contentControl.iconTintColor = animatedValue as Int }
                                ui { start() }
                            }
                        }
                        else -> if (contentControl.iconTintColor != WHITE) {
                            ofArgb(BLACK, WHITE).apply {
                                duration = ANIMATION_DURATION
                                addUpdateListener { contentControl.iconTintColor = animatedValue as Int }
                                ui { start() }
                            }
                        }
                    }
                }
            }
        }
        heartItem?.let { updateHeartItem(audioItem, it) }
        activityViewModel.getPlaylistSync()
    }

    private fun updateColor(backgroundColor: Int, primaryColor: Int, secondaryColor: Int) {
        when (contentControl.circleColor) {
            null -> contentControl.circleColor = backgroundColor
            else -> with(ofArgb(contentControl.circleColor!!, backgroundColor)) {
                duration = ANIMATION_DURATION
                addUpdateListener { contentControl.circleColor = animatedValue as Int }
                ui { start() }
            }
        }
        when (contentControl.primaryColor) {
            null -> contentControl.primaryColor = primaryColor
            else -> with(ofArgb(contentControl.primaryColor!!, primaryColor)) {
                duration = ANIMATION_DURATION
                addUpdateListener { contentControl.primaryColor = animatedValue as Int }
                ui { start() }
            }
        }
        when (contentControl.secondaryColor) {
            null -> contentControl.secondaryColor = secondaryColor
            else -> with(ofArgb(contentControl.secondaryColor!!, primaryColor)) {
                duration = ANIMATION_DURATION
                addUpdateListener { contentControl.secondaryColor = animatedValue as Int }
                ui { start() }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        with(ViewModelProvider(parentFragment as PlayFragment)[PlayViewModel::class.java]) {
            if (!isPointSet) {
                circularRevelPoint.apply {
                    with(IntArray(2)) {
                        contentControl.floatingActionButton.getLocationOnScreen(this)
                        x = first() + contentControl.floatingActionButton.width / 2
                        y = last() + contentControl.floatingActionButton.height / 2
                    }
                }
                isPointSet = true
            }
        }
    }

    override fun onDestroyView() {
        activityViewModel.removeAllObservers(TAG)
        requireContext().unregisterReceiver(broadcastReceiver)
        activityViewModel.playList = null
        super.onDestroyView()
    }

    private fun updateHeatItemIO(audioItem: AudioItem?, heartItem: MenuItem) = io { updateHeartItem(audioItem, heartItem, true) }

    private fun updateHeartItem(audioItem: AudioItem?, heartItem: MenuItem, initialize: Boolean = false) {
        val isLiked = audioItem?.let { database.playlistContent.queryLike(it.id) != null } ?: false
        when {
            initialize -> heartItem.setIsLiked(isLiked)
            else -> if (isLiked != this@PlayControlFragment.isLiked) {
                this@PlayControlFragment.isLiked = isLiked
                heartItem.setIsLiked(isLiked)
            }
        }
    }

    private fun MenuItem.setIsLiked(isLiked: Boolean) = ui {
        setIcon(if (isLiked) R.drawable.ic_heart_fill else R.drawable.ic_heart_stroke)
    }

    private fun compareAxis(x: Float, y: Float, view: View, rawAxis: IntArray): Boolean {
        view.getLocationOnScreen(rawAxis)
        return compareAxis(x, rawAxis.first().toFloat(), view.widthF) && compareAxis(y, rawAxis.last().toFloat(), view.heightF)
    }

    private fun compareAxis(axis: Float, targetX: Float, length: Float) =
        axis in (targetX .. targetX + length)

}