package ca.rheinmetall.atak.broadcast

import com.atakmap.android.ipc.AtakBroadcast

interface BroadcastReceiverWithAction : BroadcastReceiverWithFilter {
    fun getAction(): String
    override fun getDocumentedIntentFilter(): AtakBroadcast.DocumentedIntentFilter {
        val documentedIntentFilter = AtakBroadcast.DocumentedIntentFilter()

        documentedIntentFilter.addAction(getAction())

        return documentedIntentFilter
    }
}