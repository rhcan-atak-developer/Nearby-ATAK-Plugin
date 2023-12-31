package ca.rheinmetall.atak.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Supplier;

import javax.inject.Provider;
import javax.inject.Singleton;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.atakmap.android.contact.Contacts;
import com.atakmap.android.cotdetails.CoTAutoBroadcaster;
import com.atakmap.android.maps.MapData;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.menu.MenuMapAdapter;
import com.atakmap.android.menu.MenuResourceFactory;
import com.atakmap.android.preference.UnitPreferences;
import com.atakmap.coremap.maps.coords.GeoPoint;

import ca.rheinmetall.atak.PluginMapAssets;
import ca.rheinmetall.atak.application.PluginOwner;
import ca.rheinmetall.atak.thread.MainThreadScheduledExecutorService;
import ca.rheinmetall.atak.thread.NamedExecutorFactory;
import ca.rheinmetall.atak.ui.PointOfInterestViewModel;
import ca.rheinmetall.atak.ui.PointOfInterestFragment;
import ca.rheinmetall.atak.ui.incidents.IncidentsFragment;
import ca.rheinmetall.atak.ui.incidents.IncidentsViewModel;
import ca.rheinmetall.atak.ui.search_results.SearchResultFragment;
import ca.rheinmetall.atak.ui.search_results.SearchResultViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;

@Module
public interface NearbyPluginModule
{
    @Provides
    @IntoMap
    @FragmentFactoryKey(PointOfInterestFragment.class)
    static androidx.fragment.app.FragmentFactory bindIssPluginTesterFragment(final Provider<PointOfInterestFragment> provider)
    {
        return new DaggerFragmentFactory<>(provider, PointOfInterestFragment.class);
    }

    @Provides
    @IntoMap
    @FragmentFactoryKey(IncidentsFragment.class)
    static androidx.fragment.app.FragmentFactory bindIncidentsFragment(final Provider<IncidentsFragment> provider)
    {
        return new DaggerFragmentFactory<>(provider, IncidentsFragment.class);
    }

    @Provides
    static public MenuResourceFactory providesMenuResourceFactory(final MapView mapView, final PluginMapAssets pluginMapAssets)
    {
    final MenuMapAdapter mapAdapter = new MenuMapAdapter();
    return new MenuResourceFactory(mapView, mapView.getMapData(), pluginMapAssets, mapAdapter);
}

    @Provides
    static public PluginMapAssets providePluginMapAssets(@AtakContext final Context atakContext, @PluginContext final Context pluginContext)
    {
        return new PluginMapAssets(atakContext, pluginContext, null);
    }

    @Binds
    @IntoMap
    @ViewModelKey(PointOfInterestViewModel.class)
    ViewModel bindPointOfInterestViewModel(final PointOfInterestViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(IncidentsViewModel.class)
    ViewModel bindIncidentsViewModel(final IncidentsViewModel viewModel);

    @Provides
    @IntoMap
    @FragmentFactoryKey(SearchResultFragment.class)
    static androidx.fragment.app.FragmentFactory bindSearchResultFragment(final Provider<SearchResultFragment> provider)
    {
        return new DaggerFragmentFactory<>(provider, SearchResultFragment.class);
    }

    @Binds
    @IntoMap
    @ViewModelKey(SearchResultViewModel.class)
    ViewModel bindSearchResultViewModel(final SearchResultViewModel viewModel);

    @Binds
    @Singleton
    ViewModelProvider.Factory bindViewModelFactory(final ViewModelFactory factory);

    @Singleton
    @Provides
    @DefaultSharedPreferences
    static SharedPreferences provideDefaultSharedPreferences(@AtakContext final Context atakContext)
    {
        return PreferenceManager.getDefaultSharedPreferences(atakContext);
    }

    @Binds
    @Singleton
    FragmentFactory provideFragmentFactory(final MainFragmentFactory mainFragmentFactory);

    @Provides
    static Contacts provideContacts()
    {
        return Contacts.getInstance();
    }

    @Binds
    @Singleton
    @MainExecutor
    Executor bindMainExecutor(final MainThreadScheduledExecutorService mainExecutor);

    @Binds
    @Singleton
    @MainExecutor
    ScheduledExecutorService bindMainSchedulerExecutor(final MainThreadScheduledExecutorService mainExecutor);

    @Singleton
    @Binds
    @DiskIOExecutor
    Executor provideDiskIOExecutor(@DiskIOExecutor final ScheduledExecutorService diskIoExecutor);

    @Singleton
    @Provides
    @DiskIOExecutor
    static ScheduledExecutorService provideDiskIOScheduledExecutor(final NamedExecutorFactory namedExecutorFactory)
    {
        return namedExecutorFactory.createNamedExecutor(DiskIOExecutor.name);
    }

    @Provides
    static ScheduledExecutorService provideUnnamedExecutor()
    {
        return new ScheduledThreadPoolExecutor(1);
    }

    @Provides
    @Singleton
    static UnitPreferences provideUnitPreferences(final MapView mapView)
    {
        return new UnitPreferences(mapView.getContext());
    }

    @Binds
    @Singleton
    LifecycleOwner provideLifeCycleOwner(final PluginOwner pluginOwner);

    @Binds
    @Singleton
    ViewModelStoreOwner provideViewModelStoreOwner(PluginOwner pluginOwner);

    @Provides
    static CoTAutoBroadcaster bindsCotAutoBroadcaster()
    {
        return CoTAutoBroadcaster.getInstance();
    }

    @Provides
    @Singleton
    static Supplier<MapData> provideMapData()
    {
        return () -> MapView.getMapView().getMapData();
    }

    @Provides
    @Singleton
    static Supplier<GeoPoint> provideCurrentPosition()
    {
        return () -> MapView.getMapView().getSelfMarker().getGeoPointMetaData().get();
    }
}
