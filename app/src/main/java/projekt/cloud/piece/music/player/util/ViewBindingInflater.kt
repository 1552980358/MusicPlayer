package projekt.cloud.piece.music.player.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method

@Suppress("unused")
class ViewBindingInflater<VB: ViewBinding>(viewBindingClass: Class<VB>) {

    companion object ViewBindingInflaterUtil {
        private const val PARAMETER_COUNT = 3

        fun <VB: ViewBinding> Class<VB>.inflate(layoutInflater: LayoutInflater, parent: ViewGroup?, attachToRoot: Boolean) =
            ViewBindingInflater(this).inflate(layoutInflater, parent, attachToRoot)

        fun <VB: ViewBinding> Class<VB>.inflate(layoutInflater: LayoutInflater) = inflate(layoutInflater, null, false)
    }

    private val inflate: Method

    init {
        inflate = getMethodInflate(viewBindingClass)
    }

    private fun getMethodInflate(viewBindingClass: Class<VB>): Method {
        return viewBindingClass.declaredMethods.find { method ->
            method.parameterTypes.let { parameterTypes ->
                checkParameterTypesSize(parameterTypes) && checkParameterTypes(parameterTypes)
            }
        } ?: throw IllegalArgumentException("Cannot find static method \"inflate\" from $viewBindingClass")
    }

    private fun checkParameterTypesSize(parameterTypes: Array<Class<*>>): Boolean {
        return parameterTypes.size == PARAMETER_COUNT
    }

    private fun checkParameterTypes(parameterTypes: Array<Class<*>>): Boolean {
        return parameterTypes[0] == LayoutInflater::class.java
                && parameterTypes[1] == ViewGroup::class.java
                && parameterTypes[2] == Boolean::class.java
    }

    @Suppress("UNCHECKED_CAST")
    private fun inflate(layoutInflater: LayoutInflater, parent: ViewGroup?, attachToRoot: Boolean) =
        inflate.invoke(null, layoutInflater, parent, attachToRoot) as VB

}