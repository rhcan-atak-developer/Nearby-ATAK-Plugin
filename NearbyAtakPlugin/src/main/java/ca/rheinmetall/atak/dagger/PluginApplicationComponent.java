package ca.rheinmetall.atak.dagger;

import javax.inject.Singleton;

import ca.rheinmetall.atak.application.NearbyPlugin;
import dagger.Component;

@Singleton
@Component(modules = {
    NearbyPluginModule.class
})
public interface PluginApplicationComponent extends ApplicationComponent
{
    @Component.Builder
    interface Builder extends ApplicationComponent.Builder<Builder, PluginApplicationComponent>
    {
    }

    void inject(NearbyPlugin pluginLifecycle);
}
