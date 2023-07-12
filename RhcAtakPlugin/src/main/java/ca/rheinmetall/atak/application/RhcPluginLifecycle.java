package ca.rheinmetall.atak.application;

import javax.inject.Inject;

import com.atakmap.android.maps.MapView;
import com.atakmap.app.preferences.ToolsPreferenceFragment;

import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;

import ca.rheinmetall.atak.R;
import ca.rheinmetall.atak.TrafficIncidentRestClient;
import ca.rheinmetall.atak.dagger.DaggerPluginApplicationComponent;
import ca.rheinmetall.atak.map.MapViewPortDetector;
import ca.rheinmetall.atak.mapgroup.PointOfInterestMapGroup;
import ca.rheinmetall.atak.preference.PreferenceFragment;
import ca.rheinmetall.atak.ui.RhcPluginMapComponent;
import gov.tak.api.plugin.IServiceController;

public class RhcPluginLifecycle extends AbstractPlugin
{
    private static final String TAG = RhcPluginLifecycle.class.getSimpleName();

    @Inject
    RhcPluginMapComponent _rhcPluginMapComponent;

    @Inject
    PointOfInterestMapGroup _pointOfInterestMapGroup;

    @Inject
    MapViewPortDetector _mapViewPortDetector;

    @Inject
    TrafficIncidentRestClient _trafficIncidentRestClient;

    @Inject
    PreferenceFragment _preferenceFragment;

    public RhcPluginLifecycle(final IServiceController serviceController)
    {
        super(serviceController);
    }

    @Override
    protected void init(final Context atakContext, final Context pluginContext, final MapView mapView)
    {
        Log.d(TAG, "RHC plugin initialization");

        DaggerPluginApplicationComponent.builder()
                                        .mapView(mapView)
                                        .pluginContext(pluginContext)
                                        .atakContext(atakContext)
                                        .pluginOwner(_pluginOwner)
                                        .build().inject(this);


        addMapComponent(_rhcPluginMapComponent);
        final ToolsPreferenceFragment.ToolPreference pref = new ToolsPreferenceFragment.ToolPreference(
                pluginContext.getString(R.string.app_name),
                pluginContext.getString(R.string.app_name),
                "rhc_app",
                ContextCompat.getDrawable(pluginContext, R.drawable.ic_launcher),
                _preferenceFragment);
        ToolsPreferenceFragment.register(pref);

    }

    @Override
    public void onStart()
    {
        super.onStart();
        _mapViewPortDetector.start();
        _trafficIncidentRestClient.start();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        _mapViewPortDetector.stop();
        _trafficIncidentRestClient.stop();
        ToolsPreferenceFragment.unregister("rhc_app");
    }
}
