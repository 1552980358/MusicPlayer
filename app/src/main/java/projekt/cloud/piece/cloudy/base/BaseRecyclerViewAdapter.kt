package projekt.cloud.piece.cloudy.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.cloudy.base.BaseRecyclerViewAdapter.BaseRecyclerViewHolder
import projekt.cloud.piece.cloudy.util.ViewBindingInflater
import projekt.cloud.piece.cloudy.util.ViewBindingUtil.inflate

abstract class BaseRecyclerViewAdapter<B: ViewBinding>: Adapter<BaseRecyclerViewHolder>() {

    companion object BaseRecyclerViewAdapterConstant {

        const val ITEM_EMPTY = 0

    }

    /**
     * [BaseRecyclerViewAdapter.BaseRecyclerViewHolder]
     *
     * Public base class of custom [androidx.recyclerview.widget.RecyclerView.ViewHolder]
     */
    abstract class BaseRecyclerViewHolder(
        private val binding: ViewBinding
    ): ViewHolder(binding.root) {

        fun <B: ViewBinding> getBinding(): B {
            @Suppress("UNCHECKED_CAST")
            return binding as B
        }

    }

    /**
     * [BaseRecyclerViewAdapter.BaseViewBindingRecyclerViewHolder]
     *
     * Implementation of [BaseRecyclerViewAdapter.BaseRecyclerViewHolder]
     */
    private class BaseViewBindingRecyclerViewHolder<B: ViewBinding> private constructor(
        binding: B
    ): BaseRecyclerViewHolder(binding) {

        constructor(viewBindingInflater: ViewBindingInflater<B>, parent: ViewGroup): this(
            viewBindingInflater.inflate(
                LayoutInflater.from(parent.context), parent
            )
        )

    }

    /**
     * [BaseRecyclerViewAdapter.viewBindingInflater]
     * @type [ViewBindingInflater]
     *
     * Inflater of view binding [B]
     **/
    protected abstract val viewBindingInflater: ViewBindingInflater<B>

    /**
     * [androidx.recyclerview.widget.RecyclerView.Adapter.onCreateViewHolder]
     * @param parent [android.view.ViewGroup]
     * @param viewType [Int]
     *
     * @return [BaseRecyclerViewHolder]
     *
     * Create a view holder
     **/
    @CallSuper
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BaseRecyclerViewHolder {
        return BaseViewBindingRecyclerViewHolder(viewBindingInflater, parent)
            .apply(::invokeViewHolderCreated)
    }

    /**
     * [BaseRecyclerViewAdapter.invokeViewHolderCreated]
     * @param viewHolder [BaseViewBindingRecyclerViewHolder]<[B]>
     *
     * Caller of [BaseRecyclerViewAdapter.onViewBindingCreated]
     **/
    private fun invokeViewHolderCreated(viewHolder: BaseViewBindingRecyclerViewHolder<B>) {
        onViewBindingCreated(viewHolder.getBinding())
    }

    /**
     * [BaseRecyclerViewAdapter.onViewBindingCreated]
     * @param binding [B]
     *
     * Called when view binding is created
     **/
    protected open fun onViewBindingCreated(binding: B) = Unit

    /**
     * [androidx.recyclerview.widget.RecyclerView.Adapter.onBindViewHolder]
     * @param holder [BaseRecyclerViewHolder]
     * @param position [Int]
     *
     * Bind data to [BaseRecyclerViewHolder]
     **/
    @CallSuper
    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        holder.getBinding<B>().let { binding ->
            onBindViewHolder(binding.root.context, binding, position)
        }
    }

    /**
     * [BaseRecyclerViewAdapter.onBindViewHolder]
     * @param context [android.content.Context]
     * @param binding [B]
     * @param position [Int]
     *
     * Bind data to binding, provided with [android.content.Context] and [position]
     **/
    protected abstract fun onBindViewHolder(context: Context, binding: B, position: Int)

}