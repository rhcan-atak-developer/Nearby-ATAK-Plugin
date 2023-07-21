package ca.rheinmetall.atak.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.atakmap.android.cotdetails.extras.ExtraDetailsManager;
import com.atakmap.android.dropdown.DropDownMapComponent;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.menu.MapMenuReceiver;
import com.atakmap.android.overlay.DefaultMapGroupOverlay;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import ca.rheinmetall.atak.PoiMenuFactory;
import ca.rheinmetall.atak.R;
import ca.rheinmetall.atak.application.NearbyPluginBroadcastEnum;
import ca.rheinmetall.atak.mapgroup.PointOfInterestDetails;
import ca.rheinmetall.atak.mapgroup.PointOfInterestMapGroup;
import ca.rheinmetall.atak.mapgroup.TrafficIncidentDetails;
import ca.rheinmetall.atak.mapgroup.TrafficIncidentMapGroup;
import ca.rheinmetall.atak.ui.search_results.SearchResultDropDownReceiver;

@Singleton
public class NearbyPluginMapComponent extends DropDownMapComponent
{
    private final PointOfInterestDropDownReceiver _issPluginTesterDropDownReceiver;
    private final SearchResultDropDownReceiver _searchResultDropDownReceiver;
    private final PointOfInterestMapGroup _pointOfInterestMapGroup;
    private final TrafficIncidentMapGroup _trafficIncidentMapGroup;
    private final PointOfInterestDetails _pointOfInterestDetails;
    private final TrafficIncidentDetails _trafficIncidentDetails;
    private final List<DefaultMapGroupOverlay> _overlays = new ArrayList<>();

    private final PoiMenuFactory _poiMenuFactory;

    @Inject
    NearbyPluginMapComponent(final PointOfInterestDropDownReceiver issPluginTesterDropDownReceiver,
                             final PointOfInterestMapGroup pointOfInterestMapGroup,
                             final TrafficIncidentMapGroup trafficIncidentMapGroup,
                             final PointOfInterestDetails pointOfInterestDetails,
                             final TrafficIncidentDetails trafficIncidentDetails,
                             final SearchResultDropDownReceiver searchResultDropDownReceiver,
                             final PoiMenuFactory poiMenuFactory)
    {
        _issPluginTesterDropDownReceiver = issPluginTesterDropDownReceiver;
        _searchResultDropDownReceiver = searchResultDropDownReceiver;
        _pointOfInterestMapGroup = pointOfInterestMapGroup;
        _trafficIncidentMapGroup = trafficIncidentMapGroup;
        _pointOfInterestDetails = pointOfInterestDetails;
        _trafficIncidentDetails = trafficIncidentDetails;
        _poiMenuFactory = poiMenuFactory;
    }

    @Override
    public void onCreate(final Context context, final Intent intent, final MapView mapView)
    {
        context.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(context, intent, mapView);

        final AtakBroadcast.DocumentedIntentFilter listFilter = new AtakBroadcast.DocumentedIntentFilter();
        listFilter.addAction(NearbyPluginBroadcastEnum.SHOW_POI_PLUGIN.getAction());
        listFilter.addAction(NearbyPluginBroadcastEnum.SHOW_INCIDENTS_PLUGIN.getAction());
        registerDropDownReceiver(_issPluginTesterDropDownReceiver, listFilter);
        AtakBroadcast.DocumentedIntentFilter searchResultIntentFilter = new AtakBroadcast.DocumentedIntentFilter(NearbyPluginBroadcastEnum.SHOW_SEARCH_RESULTS.getAction());
        searchResultIntentFilter.addAction(NearbyPluginBroadcastEnum.CLOSE_SEARCH_RESULTS.getAction());
        registerDropDownReceiver(_searchResultDropDownReceiver, searchResultIntentFilter);

        final String iconUri = "android.resource://" + context.getPackageName() + "/" + R.drawable.ic_launcher;
        _overlays.add(new DefaultMapGroupOverlay(mapView, _pointOfInterestMapGroup, iconUri));
        _overlays.add(new DefaultMapGroupOverlay(mapView, _trafficIncidentMapGroup, iconUri));
        _overlays.forEach(mapView.getMapOverlayManager()::addOverlay);
        ExtraDetailsManager.getInstance().addProvider(_pointOfInterestDetails);
        ExtraDetailsManager.getInstance().addProvider(_trafficIncidentDetails);

        MapMenuReceiver.getInstance().registerMapMenuFactory(_poiMenuFactory);
    }

    @Override
    protected void onDestroyImpl(final Context context, final MapView mapView)
    {
        ExtraDetailsManager.getInstance().removeProvider(_pointOfInterestDetails);
        ExtraDetailsManager.getInstance().removeProvider(_trafficIncidentDetails);
        _overlays.forEach(mapView.getMapOverlayManager()::removeOverlay);
        unregisterReceiver(context, _issPluginTesterDropDownReceiver);
        unregisterReceiver(context, _searchResultDropDownReceiver);
        MapMenuReceiver.getInstance().unregisterMapMenuFactory(_poiMenuFactory);
        super.onDestroyImpl(context, mapView);
    }
}
