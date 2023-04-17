package projekt.cloud.piece.music.player.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.music.player.base.BaseRecyclerViewAdapter.BaseViewHolder

abstract class BaseRecyclerViewAdapter: Adapter<BaseViewHolder>() {

    abstract class BaseViewHolder(binding: ViewBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return onCreateViewHolder(LayoutInflater.from(parent.context), parent)
    }

    protected abstract fun onCreateViewHolder(
        layoutInflater: LayoutInflater, parent: ViewGroup
    ): BaseViewHolder

}