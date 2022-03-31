package projekt.cloud.piece.music.player.preference

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

class TransitionPreference(context: Context, attributeSet: AttributeSet?): Preference(context, attributeSet) {

    lateinit var itemView: View

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        itemView = holder.itemView
        holder.itemView.transitionName = key
    }

}