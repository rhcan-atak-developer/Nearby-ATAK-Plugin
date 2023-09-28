package ca.rheinmetall.atak.dagger;

import android.content.Context;

import com.atakmap.android.maps.MapView;

import ca.rheinmetall.atak.application.PluginOwner;
import dagger.BindsInstance;

public interface ApplicationComponent
{
    interface Builder<T extends Builder<T, U>, U extends ApplicationComponent>
    {
        @BindsInstance
        T mapView(final MapView mapView);

        @BindsInstance
        T pluginContext(@PluginContext final Context pluginContext);

        @BindsInstance
        T atakContext(@AtakContext final Context applicationContext);

        @BindsInstance
        T pluginOwner(final PluginOwner pluginOwner);

        U build();
    }
}
