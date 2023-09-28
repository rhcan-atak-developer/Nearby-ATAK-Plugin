package ca.rheinmetall.atak.application;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.rheinmetall.atak.lifecycle.PluginLifeCycle;
import gov.tak.api.annotation.DeprecatedApi;

@Singleton
public class PluginInstancesRepository implements AutoCloseable
{
    private final Set<Object> _instances = new HashSet<>();

    @Inject
    public PluginInstancesRepository()
    {
    }

    public void registerInstance(final Object instance)
    {
        _instances.add(instance);
    }

    public void registerPluginLifeCycle(final PluginLifeCycle pluginLifeCycle)
    {
        _instances.add(pluginLifeCycle);
    }

    public void registerPluginBroadcastReceiver(final PluginBroadcastReceiver pluginBroadcastReceiver)
    {
        _instances.add(pluginBroadcastReceiver);
    }

    public void unregisterInstance(final Object instance)
    {
        _instances.remove(instance);
    }

    public <T> void clearInstances(final Class<T> clazz)
    {
        _instances.removeIf(clazz::isInstance);
    }

    public void clearPluginLifeCycle()
    {
        clearInstances(PluginLifeCycle.class);
    }

    public void clearPluginBroadcastReceiver()
    {
        clearInstances(PluginBroadcastReceiver.class);
    }

    public <T> Set<T> getInstances(final Class<T> clazz)
    {
        return _instances.stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toSet());
    }

    public Set<PluginLifeCycle> getPluginLifeCycles()
    {
        return getInstances(PluginLifeCycle.class);
    }

    public Set<PluginBroadcastReceiver> getPluginBroadcastReceiver()
    {
        return getInstances(PluginBroadcastReceiver.class);
    }

    @Override
    public void close()
    {
        if (!_instances.isEmpty())
            throw new RuntimeException("Instances are leaking, make sure you processed and removed all managed instances before closing.");
    }
}
