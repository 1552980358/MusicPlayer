package projekt.cloud.piece.cloudy.ui.fragment.library.navigation

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import projekt.cloud.piece.cloudy.NavGraphLibraryNavigationDirections
import projekt.cloud.piece.cloudy.base.BaseFragment
import projekt.cloud.piece.cloudy.databinding.LibraryNavigationBinding
import projekt.cloud.piece.cloudy.util.CastUtil.cast
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

class NavigationLibraryFragment: BaseFragment<LibraryNavigationBinding>(), OnItemClickListener {

    private companion object {
        private const val POSITION_ARTIST = 0
        private const val POSITION_ALBUM = 1
        private const val POSITION_PLAYLIST = 2

        private const val EXCEPTION_MSG_INCORRECT_POSITION_ARG = "Arg \"position\" should be in range 0 to 2"
    }

    /**
     * [BaseFragment.viewBindingInflater]
     * @return [ViewBindingInflater]
     **/
    override val viewBindingInflater: ViewBindingInflater<LibraryNavigationBinding>
        get() = LibraryNavigationBinding::inflate

    /**
     * [BaseFragment.onSetupBinding]
     * @param binding [LibraryNavigationBinding]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupBinding(binding: LibraryNavigationBinding, savedInstanceState: Bundle?) {
        setupChildNavController(binding.fragmentContainerView)
        setupExposedMenu(binding)
    }

    /**
     * [NavigationLibraryFragment.childNavController]
     * @type [androidx.navigation.NavController]
     **/
    private lateinit var childNavController: NavController

    /**
     * [NavigationLibraryFragment.setupChildNavController]
     * @param fragmentContainerView [androidx.fragment.app.FragmentContainerView]
     **/
    private fun setupChildNavController(fragmentContainerView: FragmentContainerView) {
        childNavController = fragmentContainerView.getFragment<NavHostFragment>()
            .navController
    }

    /**
     * [NavigationLibraryFragment.setupExposedMenu]
     * @param binding [LibraryNavigationBinding]
     **/
    private fun setupExposedMenu(binding: LibraryNavigationBinding) {
        setupExposedMenu(
            binding.textInputLayout
                .editText
                .cast<MaterialAutoCompleteTextView>()
        )
    }

    /**
     * [NavigationLibraryFragment.setupExposedMenu]
     * @param materialAutoCompleteTextView [com.google.android.material.textfield.MaterialAutoCompleteTextView]
     **/
    private fun setupExposedMenu(
        materialAutoCompleteTextView: MaterialAutoCompleteTextView
    ) {
        materialAutoCompleteTextView.onItemClickListener = this
        materialAutoCompleteTextView.setText(
            materialAutoCompleteTextView.adapter
                ?.getItem(0)
                .toString(),
            false
        )
    }

    /**
     * [android.widget.AdapterView.OnItemClickListener.onItemClick]
     * @param parent [android.widget.AdapterView]
     * @param view [android.view.View]
     * @param position [Int]
     * @param id [Long]
     **/
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        navigate(childNavController, getChildDirection(position))
    }

    /**
     * [NavigationLibraryFragment.navigate]
     * @param navController [androidx.navigation.NavController]
     * @param navDirections [androidx.navigation.NavDirections]
     **/
    private fun navigate(navController: NavController, navDirections: NavDirections) {
        if (navController.currentDestination?.id !=
            navController.graph.getAction(navDirections.actionId)?.destinationId) {
            navController.navigate(navDirections)
        }
    }

    /**
     * [NavigationLibraryFragment.getChildDirection]
     * @param position [Int]
     * @return [androidx.navigation.NavDirections]
     *
     * Obtain the [androidx.navigation.NavDirections] implementation
     * referenced to [position]
     **/
    private fun getChildDirection(position: Int): NavDirections {
        return when (position) {
            POSITION_ARTIST -> {
                NavGraphLibraryNavigationDirections.toArtist()
            }
            POSITION_ALBUM -> {
                NavGraphLibraryNavigationDirections.toAlbum()
            }
            POSITION_PLAYLIST -> {
                // TODO: Will be correct into correct NavDirection
                NavGraphLibraryNavigationDirections.toArtist()
            }
            else -> {
                throw IllegalArgumentException(EXCEPTION_MSG_INCORRECT_POSITION_ARG)
            }
        }
    }

}