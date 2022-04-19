package projekt.cloud.piece.music.player.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class BroadcastReceiverImpl: BroadcastReceiver() {
    
    companion object {
    
        @JvmStatic
        fun broadcastReceiver(block: BroadcastReceiverImpl.() -> Unit) =
            BroadcastReceiverImpl().apply(block)
        
    }
    
    private val actionList = arrayListOf<String>()
    private lateinit var onReceive: (Context?, Intent?) -> Unit
    
    fun setActions(vararg actions: String) = actionList.addAll(actions)
    
    fun setOnReceive(onReceive: (Context?, Intent?) -> Unit) {
        this.onReceive = onReceive
    }
    
    override fun onReceive(context: Context?, intent: Intent?) =
        onReceive.invoke(context, intent)
    
    fun register(context: Context) =
        context.registerReceiver(
            this,
            IntentFilter().apply {
                actionList.forEach { addAction(it) }
            }
        )
    
    fun unregister(context: Context) =
        context.unregisterReceiver(this)
    
}