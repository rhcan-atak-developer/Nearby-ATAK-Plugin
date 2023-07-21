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
import ca.rheinmetall.atak.preference.PreferenceFragment;
import ca.rheinmetall.atak.ui.NearbyPluginMapComponent;
import gov.tak.api.plugin.IServiceController;

public class NearbyPlugin extends AbstractPlugin
{
    private static final String TAG = NearbyPlugin.class.getSimpleName();

    @Inject
    NearbyPluginMapComponent _nearbyPluginMapComponent;

    @Inject
    PreferenceFragment _preferenceFragment;

    @Inject
    TrafficIncidentRestClient _trafficIncidentRestClient;

    public NearbyPlugin(final IServiceController serviceController)
    {
        super(serviceController);
    }

    @Override
    protected void init(final Context atakContext, final Context pluginContext, final MapView mapView)
    {
        Log.d(TAG, "Nearby plugin initialization");

        DaggerPluginApplicationComponent.builder()
                                        .mapView(mapView)
                                        .pluginContext(pluginContext)
                                        .atakContext(atakContext)
                                        .pluginOwner(_pluginOwner)
                                        .build().inject(this);


        addMapComponent(_nearbyPluginMapComponent);
        final ToolsPreferenceFragment.ToolPreference pref = new ToolsPreferenceFragment.ToolPreference(
                pluginContext.getString(R.string.app_name),
                pluginContext.getString(R.string.app_name),
                "nearby_plugin_tool_preference_key",
                ContextCompat.getDrawable(pluginContext, R.drawable.ic_launcher),
                _preferenceFragment);
        ToolsPreferenceFragment.register(pref);

    }

    @Override
    public void onStop()
    {
        super.onStop();
        ToolsPreferenceFragment.unregister("nearby_plugin_tool_preference_key");
    }
}
