package ca.rheinmetall.atak.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapView;

import android.content.Context;
import android.content.Intent;

import ca.rheinmetall.atak.application.RhcPluginBroadcastEnum;
import ca.rheinmetall.atak.dagger.FragmentFactory;

@Singleton
public class RhcPluginDropDownReceiver extends DropDownReceiver
{
    private final FragmentFactory _fragmentFactory;
    private PointOfInterestFragment _fragment;

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
        if(RhcPluginBroadcastEnum.SHOW_RHC_PLUGIN.getAction().equals(intent.getAction()))
        {
            _fragment = _fragmentFactory.instantiate(PointOfInterestFragment.class);
            showDropDown(_fragment, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH, HALF_HEIGHT);
        }
    }

    @Override
    protected void disposeImpl()
    {
    }
}
