package projekt.cloud.piece.music.player.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.Hold
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BasePreferenceFragment
import projekt.cloud.piece.music.player.preference.util.PreferenceFragmentUtil.transitionPreference

class SettingsFragment: BasePreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
        transitionPreference(R.string.key_setting_file) {
            setOnPreferenceClickListener {
                navController.navigate(
                    SettingsFragmentDirections.actionToFileSettings(),
                    FragmentNavigatorExtras(itemView to itemView.transitionName)
                )
                true
            }
        }
        transitionPreference(R.string.key_setting_play) {
            setOnPreferenceClickListener {
                navController.navigate(
                    SettingsFragmentDirections.actionToPlaySettings(),
                    FragmentNavigatorExtras(itemView to itemView.transitionName)
                )
                true
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        postponeEnterTransition()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Use RecyclerView as anchor to decide whether the content is pre-drawing
        with(recyclerView) {
            viewTreeObserver?.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
    }

    private val recyclerView get() = PreferenceFragmentCompat::class.java.getDeclaredField("mList")
        .apply { isAccessible = true }
        .get(this as PreferenceFragmentCompat) as RecyclerView

    override fun setToolbarNavigationIcon() = R.drawable.ic_close

    override fun setTitle() = R.string.nav_settings

}