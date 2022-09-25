package projekt.cloud.piece.music.player.ui.main.base

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentBaseMainBinding
import projekt.cloud.piece.music.player.ui.main.MainFragment
import projekt.cloud.piece.music.player.ui.main.MainViewModel

open class BaseMainFragment: BaseFragment() {
    
    protected lateinit var viewModel: MainViewModel
    
    private val mainFragment: MainFragment
        // Token from https://stackoverflow.com/a/63200538/11685230
        get() = requireParentFragment()                 // NavHostFragment
            .requireParentFragment() as MainFragment    // MainFragment
    
    private var _binding: FragmentBaseMainBinding? = null
    protected val binding: FragmentBaseMainBinding
        get() = _binding!!
    private val root: CoordinatorLayout
        get() = binding.root
    protected val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(mainFragment)[MainViewModel::class.java]
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBaseMainBinding.inflate(inflater)
        return root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.setOnApplyWindowInsetsListener { _, insets ->
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> { setMargins(0, 0, 0, getInsetBottom(insets)) }
            insets
        }
    }
    
    private fun getInsetBottom(windowInsets: WindowInsets) = when {
        Build.VERSION.SDK_INT > Build.VERSION_CODES.Q -> windowInsets.getInsets(WindowInsets.Type.systemBars()).bottom
        else -> @Suppress("DEPRECATION") windowInsets.systemWindowInsetBottom
    }
    
    protected fun setBottomPlayBarEnable() {
        mainFragment.setBottomPlayBarEnable(true)
    }
    
}