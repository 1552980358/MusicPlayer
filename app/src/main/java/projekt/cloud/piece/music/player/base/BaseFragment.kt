package projekt.cloud.piece.music.player.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.music.player.util.FragmentUtil.viewLifecycleProperty
import projekt.cloud.piece.music.player.util.ViewBindingInflater.ViewBindingInflaterUtil.inflate

abstract class BaseFragment<VB: ViewBinding>: Fragment() {

    protected var binding: VB by viewLifecycleProperty()

    protected abstract val viewBindingClass: Class<VB>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = viewBindingClass.inflate(inflater, container, false).also { binding ->
            if (binding is ViewDataBinding) {
                binding.lifecycleOwner = viewLifecycleOwner
            }
        }
        return binding.root
    }

}