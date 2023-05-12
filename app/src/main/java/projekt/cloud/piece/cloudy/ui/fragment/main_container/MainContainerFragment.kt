package projekt.cloud.piece.cloudy.ui.fragment.main_container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import projekt.cloud.piece.cloudy.databinding.FragmentMainContainerBinding

class MainContainerFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return FragmentMainContainerBinding.inflate(inflater, container, false)
            .root
    }

}