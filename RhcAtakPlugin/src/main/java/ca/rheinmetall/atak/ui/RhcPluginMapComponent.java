package ca.rheinmetall.atak.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.atakmap.android.dropdown.DropDownMapComponent;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;

import android.content.Context;
import android.content.Intent;

import ca.rheinmetall.atak.R;
import ca.rheinmetall.atak.application.RhcPluginBroadcastEnum;

@Singleton
public class RhcPluginMapComponent extends DropDownMapComponent
{
    private final RhcPluginDropDownReceiver _issPluginTesterDropDownReceiver;

    @Inject
    RhcPluginMapComponent(final RhcPluginDropDownReceiver issPluginTesterDropDownReceiver)
    {
        _issPluginTesterDropDownReceiver = issPluginTesterDropDownReceiver;
    }

    @Override
    public void onCreate(final Context context, final Intent intent, final MapView mapView)
    {
        context.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(context, intent, mapView);

        final AtakBroadcast.DocumentedIntentFilter listFilter = new AtakBroadcast.DocumentedIntentFilter();
        listFilter.addAction(RhcPluginBroadcastEnum.SHOW_RHC_PLUGIN.getAction());
        registerDropDownReceiver(_issPluginTesterDropDownReceiver, listFilter);
    }

    @Override
    protected void onDestroyImpl(final Context context, final MapView view)
    {
        unregisterReceiver(context, _issPluginTesterDropDownReceiver);

        super.onDestroyImpl(context, view);
    }
}
