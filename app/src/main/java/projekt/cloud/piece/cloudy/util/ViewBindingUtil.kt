package projekt.cloud.piece.cloudy.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

typealias ViewBindingInflater<B> = (LayoutInflater, ViewGroup?, Boolean) -> B

object ViewBindingUtil {

    fun <B: ViewBinding> ViewBindingInflater<B>.inflate(
        layoutInflater: LayoutInflater, viewGroup: ViewGroup?
    ): B = inflate(layoutInflater, viewGroup, false)

    fun <B: ViewBinding> ViewBindingInflater<B>.inflate(
        layoutInflater: LayoutInflater, viewGroup: ViewGroup?, attachToRoot: Boolean
    ): B = invoke(layoutInflater, viewGroup, attachToRoot)

}