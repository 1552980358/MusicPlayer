package projekt.cloud.piece.music.player.ui.fragment.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.base.BaseLayoutCompat.BaseLayoutCompatUtil.layoutCompat
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.ScreenDensity.ScreenDensityUtil.screenDensity

class HomeFragment: BaseMultiDensityFragment<FragmentHomeBinding, HomeLayoutCompat>() {

    override val viewBindingClass: Class<FragmentHomeBinding>
        get() = FragmentHomeBinding::class.java

    override fun onCreateLayoutCompat(binding: FragmentHomeBinding): HomeLayoutCompat {
        return binding.layoutCompat(requireContext().screenDensity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.main {
            val audioMetadataList = withContext(default) {
                requireContext().runtimeDatabase
                    .audioMetadataDao()
                    .query()
            }

            layoutCompat.setupRecyclerViewAdapter(
                HomeRecyclerViewUtil.getRecyclerViewAdapter(
                    this@HomeFragment, audioMetadataList
                )
            )

        }
    }

}