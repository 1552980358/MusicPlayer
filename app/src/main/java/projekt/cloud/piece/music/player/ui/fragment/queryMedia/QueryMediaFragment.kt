package projekt.cloud.piece.music.player.ui.fragment.queryMedia

import android.os.Bundle
import android.provider.MediaStore.Audio.AudioColumns.ARTIST
import android.provider.MediaStore.Audio.AudioColumns.ARTIST_ID
import android.provider.MediaStore.Audio.AudioColumns.ALBUM
import android.provider.MediaStore.Audio.AudioColumns.ALBUM_ID
import android.provider.MediaStore.Audio.AudioColumns.DURATION
import android.provider.MediaStore.Audio.AudioColumns.SIZE
import android.provider.MediaStore.Audio.AudioColumns._ID
import android.provider.MediaStore.Audio.AudioColumns.TITLE
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.Audio.Media.IS_MUSIC
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentQueryMediaBinding
import projekt.cloud.piece.music.player.storage.audio.AudioDatabase
import projekt.cloud.piece.music.player.storage.audio.AudioDatabase.AudioDatabaseUtil.audioDatabase
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.Companion.runtimeDatabase
import projekt.cloud.piece.music.player.util.ContextUtil.requireWindowInsets
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main

class QueryMediaFragment: BaseFragment<FragmentQueryMediaBinding>() {

    override val viewBindingClass: Class<FragmentQueryMediaBinding>
        get() = FragmentQueryMediaBinding::class.java

    private val container: ConstraintLayout
        get() = binding.constraintLayout
    private val appBarLayout: AppBarLayout
        get() = binding.appBarLayout
    private val subtitle: MaterialTextView
        get() = binding.materialTextViewSubtitle

    private val finish: MaterialButton
        get() = binding.materialButtonFinish

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var isComplete = false

        requireContext().requireWindowInsets { insets ->
            container.updatePadding(bottom = insets.bottom)
            appBarLayout.updatePadding(top = insets.top)
        }

        with(finish) {
            textScaleX = 0F
            icon = IndeterminateDrawable.createCircularDrawable(
                requireContext(),
                CircularProgressIndicatorSpec(requireContext(), null).apply {
                    indicatorColors[0] = TypedValue().let { typeValue ->
                        requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typeValue, true)
                        typeValue.data
                    }
                }
            )
        }

        lifecycleScope.main {

            val audioDatabase = withContext(default) {
                requireContext().audioDatabase
            }

            subtitle.text = getString(
                R.string.query_media_query_body_complete,
                withContext(default) {
                    queryMediaStore(audioDatabase)
                }
            )

            withContext(default) {
                requireContext().runtimeDatabase.audioMetadataDao()
                    .insert(audioDatabase.metadataDao().query())
            }

            with(finish) {
                icon = null
                textScaleX = 1F
            }

            isComplete = true
        }

        finish.setOnClickListener {
            if (isComplete) {
                // Completed
            }
        }
    }

    private suspend fun queryMediaStore(audioDatabase: AudioDatabase): Int {
        var count = 0
        requireContext().contentResolver
            .query(EXTERNAL_CONTENT_URI, null, null, null, IS_MUSIC)
            ?.use { cursor ->
                while (cursor.moveToNext()) {
                    audioDatabase.metadataDao().insert(
                        cursor.getString(cursor.getColumnIndexOrThrow(_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ARTIST)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ALBUM)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ALBUM_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(DURATION)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(SIZE))
                    )
                    count++
                }
            }
        return count
    }

}