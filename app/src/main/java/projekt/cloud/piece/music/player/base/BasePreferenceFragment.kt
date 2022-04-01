package projekt.cloud.piece.music.player.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import projekt.cloud.piece.music.player.MainActivityViewModel
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.FragmentBasePreferenceBinding

abstract class BasePreferenceFragment: PreferenceFragmentCompat() {

    private var _binding: FragmentBasePreferenceBinding? = null
    protected val binding get() = _binding!!

    protected lateinit var activityViewModel: MainActivityViewModel

    protected lateinit var navController: NavController

    private val toolbarNavigationIcon get() = setToolbarNavigationIcon()
    private val toolbarTitle get() = setTitle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        activityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_preference, container, false)
        // Add PreferenceFragmentCompat original preference screen content
        binding.relativeLayout.addView(super.onCreateView(inflater, container, savedInstanceState))
        with(binding.toolbar) {
            setNavigationIcon(toolbarNavigationIcon)
            setTitle(toolbarTitle)
            setNavigationOnClickListener { navController.navigateUp() }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun setToolbarNavigationIcon() = R.drawable.ic_back

    abstract fun setTitle(): Int

}