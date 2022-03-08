package projekt.cloud.piece.music.player.util

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

object DataBindingUtil {
    
    @JvmStatic
    @BindingAdapter("app:isRefreshing")
    fun SwipeRefreshLayout.setRefresh(isRefreshing: Boolean) {
        this.isRefreshing = isRefreshing
    }
    
}