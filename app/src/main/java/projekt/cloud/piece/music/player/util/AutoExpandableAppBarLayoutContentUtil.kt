package projekt.cloud.piece.music.player.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.Transition.TransitionListener
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs
import projekt.cloud.piece.music.player.util.KotlinUtil.ifNotNull
import projekt.cloud.piece.music.player.util.KotlinUtil.tryTo

object AutoExpandableAppBarLayoutContentUtil {

    class AutoExpandableAppBarLayoutBehavior(
        context: Context, attributeSet: AttributeSet?
    ): AppBarLayout.Behavior(context, attributeSet) {

        private var isContentExpanded = false
        fun setObserver(fragment: Fragment, isExpandedLiveData: LiveData<Boolean>) {
            isExpandedLiveData.observe(fragment.viewLifecycleOwner) { isExpanded ->
                isContentExpanded = isExpanded
            }
        }

        override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
            super.onStopNestedScroll(coordinatorLayout, abl, target, type)
            abl.setExpanded(isContentExpanded)
        }

    }

    @JvmOverloads
    fun AppBarLayout.setupAutoExpandableAppBarLayout(
        fragment: Fragment,
        constraintLayout: ConstraintLayout,
        isExpandedLiveData: MutableLiveData<Boolean>,
        expandedConstraintSet: ConstraintSet, collapsedConstraintSet: ConstraintSet,
        transitionLimit: () -> Int,
        currentSet: ConstraintSet = expandedConstraintSet
    ) {

        // Update live data first
        isExpandedLiveData.value = currentSet == expandedConstraintSet

        var currentConstraintSet = currentSet
        var transitionSet: TransitionSet? = null

        val offsetCutOff by lazy(transitionLimit)

        addOnOffsetChangedListener { _, verticalOffset ->
            transitionSet.ifNotNull {
                return@addOnOffsetChangedListener
            }

            when (currentConstraintSet) {
                expandedConstraintSet -> {
                    collapsedConstraintSet.takeIf {
                        abs(verticalOffset) > offsetCutOff
                    }
                }
                else -> {
                    expandedConstraintSet.takeIf {
                        abs(verticalOffset) < offsetCutOff
                    }
                }
            }?.let { constraintSet ->
                currentConstraintSet = constraintSet
                isExpandedLiveData.value = constraintSet == expandedConstraintSet
                transitionSet = getEndTaskedTransitionSet { transitionSet = null }
                TransitionManager.beginDelayedTransition(constraintLayout, transitionSet)
                constraintSet.applyTo(constraintLayout)
            }
        }

        layoutParams.tryTo<CoordinatorLayout.LayoutParams>()
            ?.let { layoutParams -> layoutParams.behavior.tryTo<AutoExpandableAppBarLayoutBehavior>() }
            ?.setObserver(fragment, isExpandedLiveData)
    }

    private fun getEndTaskedTransitionSet(doOnEnd: () -> Unit): TransitionSet {
        return AutoTransition().addListener(
            object : TransitionListener {
                override fun onTransitionStart(transition: Transition) = Unit
                override fun onTransitionEnd(transition: Transition) = doOnEnd.invoke()
                override fun onTransitionCancel(transition: Transition) = Unit
                override fun onTransitionPause(transition: Transition) = Unit
                override fun onTransitionResume(transition: Transition) = Unit
            }
        )
    }

}