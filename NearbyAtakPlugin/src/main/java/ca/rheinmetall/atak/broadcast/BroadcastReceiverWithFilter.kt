package ca.rheinmetall.atak.broadcast

import android.content.BroadcastReceiver
import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter

interface BroadcastReceiverWithFilter {
    fun getBroadcastReceiver(): BroadcastReceiver
    fun getDocumentedIntentFilter(): DocumentedIntentFilter
}