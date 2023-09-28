package ca.rheinmetall.atak.dagger;

import javax.inject.Singleton;

import ca.rheinmetall.atak.application.RhcPluginLifecycle;
import dagger.Component;

@Singleton
@Component(modules = {
    RhcPluginModule.class
})
public interface PluginApplicationComponent extends ApplicationComponent
{
    @Component.Builder
    interface Builder extends ApplicationComponent.Builder<Builder, PluginApplicationComponent>
    {
    }

    void inject(RhcPluginLifecycle pluginLifecycle);
}
