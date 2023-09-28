package ca.rheinmetall.atak.lifecycle;

import javax.inject.Inject;

import ca.rheinmetall.atak.application.PluginInstancesRepository;

public abstract class PluginLifeCycled implements PluginLifeCycle
{
    @Inject
    public void registerSelf(final PluginInstancesRepository pluginInstancesRepository)
    {
        pluginInstancesRepository.registerPluginLifeCycle(this);
    }

    @Override
    public void start()
    {
    }

    @Override
    public void stop()
    {
    }
}
