package ca.rheinmetall.atak.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapView;

import android.content.Context;
import android.content.Intent;

import ca.rheinmetall.atak.application.RhcPluginBroadcastEnum;
import ca.rheinmetall.atak.dagger.FragmentFactory;
import ca.rheinmetall.atak.ui.incidents.IncidentsFragment;

@Singleton
public class RhcPluginDropDownReceiver extends DropDownReceiver
{
    private final FragmentFactory _fragmentFactory;
    private PointOfInterestFragment _pointOfInterestFragment;
    private IncidentsFragment _incidentsFragment;

    @Inject
    public RhcPluginDropDownReceiver(final MapView mapView, final FragmentFactory fragmentFactory)
    {
        super(mapView);
        _fragmentFactory = fragmentFactory;
        setRetain(true);
    }

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        if(RhcPluginBroadcastEnum.SHOW_POI_PLUGIN.getAction().equals(intent.getAction()))
        {
            _pointOfInterestFragment = _fragmentFactory.instantiate(PointOfInterestFragment.class);
            showDropDown(_pointOfInterestFragment, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH, HALF_HEIGHT);
        }
        if(RhcPluginBroadcastEnum.SHOW_INCIDENTS_PLUGIN.getAction().equals(intent.getAction()))
        {
            _incidentsFragment = _fragmentFactory.instantiate(IncidentsFragment.class);
            showDropDown(_incidentsFragment, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH, HALF_HEIGHT);
        }
    }

    @Override
    protected void disposeImpl()
    {
    }
}
