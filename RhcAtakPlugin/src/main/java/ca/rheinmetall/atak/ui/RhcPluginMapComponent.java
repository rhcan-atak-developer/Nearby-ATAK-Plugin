package ca.rheinmetall.atak.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.atakmap.android.cotdetails.extras.ExtraDetailsManager;
import com.atakmap.android.dropdown.DropDownMapComponent;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.overlay.DefaultMapGroupOverlay;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import ca.rheinmetall.atak.R;
import ca.rheinmetall.atak.application.RhcPluginBroadcastEnum;
import ca.rheinmetall.atak.mapgroup.PointOfInterestDetails;
import ca.rheinmetall.atak.mapgroup.PointOfInterestMapGroup;
import ca.rheinmetall.atak.mapgroup.TrafficIncidentDetails;
import ca.rheinmetall.atak.mapgroup.TrafficIncidentMapGroup;
import ca.rheinmetall.atak.ui.search_results.SearchResultDropDownReceiver;

@Singleton
public class RhcPluginMapComponent extends DropDownMapComponent
{
    private final RhcPluginDropDownReceiver _issPluginTesterDropDownReceiver;
    private SearchResultDropDownReceiver _searchResultDropDownReceiver;
    private final PointOfInterestMapGroup _pointOfInterestMapGroup;
    private final TrafficIncidentMapGroup _trafficIncidentMapGroup;
    private final PointOfInterestDetails _pointOfInterestDetails;
    private TrafficIncidentDetails _trafficIncidentDetails;
    private final List<DefaultMapGroupOverlay> _overlays = new ArrayList<>();

    @Inject
    RhcPluginMapComponent(final RhcPluginDropDownReceiver issPluginTesterDropDownReceiver,
                          final PointOfInterestMapGroup pointOfInterestMapGroup,
                          final TrafficIncidentMapGroup trafficIncidentMapGroup,
                          final SearchResultDropDownReceiver searchResultDropDownReceiver,
                          final PointOfInterestDetails pointOfInterestDetails,
                          final TrafficIncidentDetails trafficIncidentDetails)
    {
        _issPluginTesterDropDownReceiver = issPluginTesterDropDownReceiver;
        _searchResultDropDownReceiver = searchResultDropDownReceiver;
        _pointOfInterestMapGroup = pointOfInterestMapGroup;
        _trafficIncidentMapGroup = trafficIncidentMapGroup;
        _pointOfInterestDetails = pointOfInterestDetails;
        _trafficIncidentDetails = trafficIncidentDetails;
    }

    @Override
    public void onCreate(final Context context, final Intent intent, final MapView mapView)
    {
        context.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(context, intent, mapView);

        final AtakBroadcast.DocumentedIntentFilter listFilter = new AtakBroadcast.DocumentedIntentFilter();
        listFilter.addAction(RhcPluginBroadcastEnum.SHOW_POI_PLUGIN.getAction());
        listFilter.addAction(RhcPluginBroadcastEnum.SHOW_INCIDENTS_PLUGIN.getAction());
        registerDropDownReceiver(_issPluginTesterDropDownReceiver, listFilter);
        AtakBroadcast.DocumentedIntentFilter searchResultIntentFilter = new AtakBroadcast.DocumentedIntentFilter(RhcPluginBroadcastEnum.SHOW_SEARCH_RESULTS.getAction());
        searchResultIntentFilter.addAction(RhcPluginBroadcastEnum.CLOSE_SEARCH_RESULTS.getAction());
        registerDropDownReceiver(_searchResultDropDownReceiver, searchResultIntentFilter);

        final String iconUri = "android.resource://" + context.getPackageName() + "/" + R.drawable.ic_launcher;
        _overlays.add(new DefaultMapGroupOverlay(mapView, _pointOfInterestMapGroup, iconUri));
        _overlays.add(new DefaultMapGroupOverlay(mapView, _trafficIncidentMapGroup, iconUri));
        _overlays.forEach(mapView.getMapOverlayManager()::addOverlay);
        ExtraDetailsManager.getInstance().addProvider(_pointOfInterestDetails);
        ExtraDetailsManager.getInstance().addProvider(_trafficIncidentDetails);
    }

    @Override
    protected void onDestroyImpl(final Context context, final MapView mapView)
    {
        ExtraDetailsManager.getInstance().removeProvider(_pointOfInterestDetails);
        ExtraDetailsManager.getInstance().removeProvider(_trafficIncidentDetails);
        _overlays.forEach(mapView.getMapOverlayManager()::removeOverlay);
        unregisterReceiver(context, _issPluginTesterDropDownReceiver);
        unregisterReceiver(context, _searchResultDropDownReceiver);
        super.onDestroyImpl(context, mapView);
    }
}
