package ca.rheinmetall.atak.map;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.atakmap.android.maps.MapEvent;
import com.atakmap.android.maps.MapEventDispatcher;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.maps.coords.GeoBounds;
import com.atakmap.map.AtakMapView;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.rheinmetall.atak.PointOfInterestRestClient;
import ca.rheinmetall.atak.lifecycle.PluginLifeCycled;

@Singleton
public class MapViewPortDetector extends PluginLifeCycled implements MapEventDispatcher.MapEventDispatchListener, AtakMapView.OnMapProjectionChangedListener, AtakMapView.OnMapViewResizedListener {
    public final MutableLiveData<MapViewPort> _mapViewPortMutableLiveData = new MutableLiveData<>(null);
    private final MapView _mapView;
    private final PointOfInterestRestClient _pointOfInterestRestClient;

    @Inject
    MapViewPortDetector(final MapView mapView, final PointOfInterestRestClient pointOfInterestRestClient)
    {
        _mapView = mapView;
        _pointOfInterestRestClient = pointOfInterestRestClient;
        updateViewPort();
    }

    public LiveData<MapViewPort> getMapViewPortLiveData()
    {
        return _mapViewPortMutableLiveData;
    }

    @Override
    public void start() {
        _mapView.getMapEventDispatcher().addMapEventListener(this);
        _mapView.addOnMapProjectionChangedListener(this);
        _mapView.addOnMapViewResizedListener(this);
    }

    @Override
    public void stop() {
        _mapView.getMapEventDispatcher().removeMapEventListener(this);
        _mapView.removeOnMapProjectionChangedListener(this);
        _mapView.removeOnMapViewResizedListener(this);
    }

    @Override
    public void onMapEvent(MapEvent mapEvent) {
        if(mapEvent.getType().equalsIgnoreCase("map_release"))
            updateViewPort();
    }

    @Override
    public void onMapProjectionChanged(AtakMapView atakMapView) {
        updateViewPort();
    }

    @Override
    public void onMapViewResized(AtakMapView atakMapView) {
        updateViewPort();
    }


    private void updateViewPort() {
        final GeoBounds bounds = _mapView.getBounds();
        final MapViewPort mapViewPort = new MapViewPort(
            new Point(bounds.getNorth(), bounds.getWest()),
            new Point(bounds.getSouth(), bounds.getWest()),
            new Point(bounds.getNorth(), bounds.getEast()),
            new Point(bounds.getSouth(), bounds.getEast()));
        _mapViewPortMutableLiveData.setValue(mapViewPort);
        Log.d("MapViewPortDetector", mapViewPort.toString());

        _pointOfInterestRestClient.setViewPort(mapViewPort);
    }
}
