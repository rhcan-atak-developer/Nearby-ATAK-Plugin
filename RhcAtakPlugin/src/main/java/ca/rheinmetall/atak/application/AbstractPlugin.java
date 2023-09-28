package ca.rheinmetall.atak.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Pair;

import com.atak.plugins.impl.PluginContextProvider;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapComponent;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.widgets.AbstractWidgetMapComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ca.rheinmetall.atak.broadcast.BroadcastReceiverWithFilter;
import ca.rheinmetall.atak.broadcast.BroadcastReceiverWithIntentFilter;
import ca.rheinmetall.atak.dagger.DiskIOExecutor;
import ca.rheinmetall.atak.dagger.MainExecutor;
import ca.rheinmetall.atak.lifecycle.PluginLifeCycle;
import gov.tak.api.plugin.IPlugin;
import gov.tak.api.plugin.IServiceController;
import transapps.maps.plugin.tool.ToolDescriptor;

public abstract class AbstractPlugin implements IPlugin
{
    private final IServiceController _serviceController;
    private final List<Pair<MapComponent, LoadTime>> _components = new ArrayList<>();
    private final List<ToolDescriptor> _toolDescriptors = new ArrayList<>();
    private final List<BroadcastReceiverWithFilter> _broadcastReceivers = new ArrayList<>();
    protected final PluginOwner _pluginOwner = new PluginOwner();

    @Inject
    PluginInstancesRepository _pluginInstancesRepository;
    @Inject @MainExecutor
    ScheduledExecutorService _mainThreadScheduledExecutorService;
    @Inject @DiskIOExecutor
    ScheduledExecutorService _diskIOThreadScheduledExecutorService;

    private boolean _shutdownMainExecutorOnDestroy = true;
    private ScheduledFuture<?> _delayedMapComponentRegistrationFuture;

    public AbstractPlugin(final IServiceController serviceController)
    {
        this(serviceController, new UnhandledExceptionHandler());
    }

    public AbstractPlugin(final IServiceController serviceController, final UnhandledExceptionHandler unhandledExceptionHandler)
    {
        _serviceController = serviceController;
        Thread.setDefaultUncaughtExceptionHandler(unhandledExceptionHandler);
    }

    @Override
    public void onStart()
    {
        final MapView mapView = MapView.getMapView();
        final Context atakContext = mapView.getContext().getApplicationContext();
        final PluginContextProvider contextProvider = _serviceController.getService(PluginContextProvider.class);
        final Context pluginContext = contextProvider.getPluginContext();

        init(atakContext, pluginContext, mapView); // Dagger injection must happens here

        _components.stream().filter(pair -> pair.second == LoadTime.ON_START).forEach(pair -> _serviceController.registerComponent(MapComponent.class, pair.first));
        _toolDescriptors.forEach(t -> _serviceController.registerComponent(ToolDescriptor.class, t));
        _broadcastReceivers.forEach(br -> AtakBroadcast.getInstance().registerReceiver(br.getBroadcastReceiver(), br.getDocumentedIntentFilter()));

        _pluginOwner.setCurrentState(androidx.lifecycle.Lifecycle.State.STARTED);

        _pluginInstancesRepository.getPluginLifeCycles().forEach(PluginLifeCycle::start);
        _pluginInstancesRepository.getPluginBroadcastReceiver().forEach(receiver -> AtakBroadcast.getInstance().registerReceiver(receiver, new AtakBroadcast.DocumentedIntentFilter(receiver.getSupportedAction())));

        final List<Pair<MapComponent, LoadTime>> delayedComponents = _components.stream().filter(pair -> pair.second == LoadTime.DELAYED).collect(Collectors.toList());

        if (!delayedComponents.isEmpty())
        {
            _delayedMapComponentRegistrationFuture = _mainThreadScheduledExecutorService.schedule(() ->
                    delayedComponents.forEach(pair -> _serviceController.registerComponent(MapComponent.class, pair.first)), 10, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onStop()
    {
        Optional.ofNullable(_delayedMapComponentRegistrationFuture).ifPresent(future -> future.cancel(true));
        _pluginInstancesRepository.getPluginBroadcastReceiver().forEach(AtakBroadcast.getInstance()::unregisterReceiver);
        _pluginInstancesRepository.clearPluginBroadcastReceiver();

        _pluginInstancesRepository.getPluginLifeCycles().forEach(PluginLifeCycle::stop);
        _pluginInstancesRepository.clearPluginLifeCycle();

        _pluginInstancesRepository.close();

        _pluginOwner.setCurrentState(androidx.lifecycle.Lifecycle.State.DESTROYED);

        _components.stream().map(pair -> pair.first).forEach(c -> _serviceController.unregisterComponent(MapComponent.class, c));
        _toolDescriptors.forEach(t -> _serviceController.unregisterComponent(ToolDescriptor.class, t));
        _broadcastReceivers.forEach(br -> AtakBroadcast.getInstance().unregisterReceiver(br.getBroadcastReceiver()));

        if (_shutdownMainExecutorOnDestroy)
            _mainThreadScheduledExecutorService.shutdown();

        _diskIOThreadScheduledExecutorService.shutdown();
    }

    protected abstract void init(final Context atakContext, final Context pluginContext, final MapView mapView);

    protected void addMapComponent(final MapComponent mapComponent)
    {
        _components.add(new Pair<>(mapComponent, mapComponent instanceof AbstractWidgetMapComponent ? LoadTime.DELAYED : LoadTime.ON_START));
    }

    protected void addToolDescriptors(final ToolDescriptor toolDescriptor)
    {
        _toolDescriptors.add(toolDescriptor);
    }

    protected void addBroadcastReceiver(final BroadcastReceiver broadcastReceiver, final String action)
    {
        _broadcastReceivers.add(new BroadcastReceiverWithIntentFilter(broadcastReceiver, action));
    }

    protected void addBroadcastReceiver(final BroadcastReceiver broadcastReceiver, final String... actions)
    {
        _broadcastReceivers.add(new BroadcastReceiverWithIntentFilter(broadcastReceiver, actions));
    }

    protected void addBroadcastReceiver(final BroadcastReceiverWithFilter broadcastReceiver)
    {
        _broadcastReceivers.add(broadcastReceiver);
    }

    public void setShutdownMainExecutorOnDestroy(final boolean shutdownMainExecutorOnDestroy)
    {
        _shutdownMainExecutorOnDestroy = shutdownMainExecutorOnDestroy;
    }

    private enum LoadTime
    {
        ON_START,
        DELAYED
    }
}
