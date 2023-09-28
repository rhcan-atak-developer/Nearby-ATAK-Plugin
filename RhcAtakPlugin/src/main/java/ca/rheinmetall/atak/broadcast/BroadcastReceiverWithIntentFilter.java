package ca.rheinmetall.atak.broadcast;

import android.content.BroadcastReceiver;

import androidx.annotation.NonNull;

import com.atakmap.android.ipc.AtakBroadcast;

public class BroadcastReceiverWithIntentFilter implements BroadcastReceiverWithFilter
{
    private final BroadcastReceiver _broadcastReceiver;
    private final AtakBroadcast.DocumentedIntentFilter _documentedIntentFilter;

    public BroadcastReceiverWithIntentFilter(final BroadcastReceiver broadcastReceiver, final String... actions)
    {
        _broadcastReceiver = broadcastReceiver;
        _documentedIntentFilter = new AtakBroadcast.DocumentedIntentFilter();
        for (final String action : actions)
        {
            _documentedIntentFilter.addAction(action);
        }
    }

    @Override
    @NonNull
    public BroadcastReceiver getBroadcastReceiver()
    {
        return _broadcastReceiver;
    }

    @Override
    @NonNull
    public AtakBroadcast.DocumentedIntentFilter getDocumentedIntentFilter()
    {
        return _documentedIntentFilter;
    }
}
