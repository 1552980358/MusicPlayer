package projekt.cloud.piece.music.player.preference

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

class TransitionPreference(context: Context, attributeSet: AttributeSet?): Preference(context, attributeSet) {

    var rootTransitionName: String? = null
        set(value) {
            field = value
            notifyChanged()
        }

    lateinit var itemView: View

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        itemView = holder.itemView
        rootTransitionName?.let { holder.itemView.transitionName = it }
    }

}