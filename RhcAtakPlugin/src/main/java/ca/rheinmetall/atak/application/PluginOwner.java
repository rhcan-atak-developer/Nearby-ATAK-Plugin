package ca.rheinmetall.atak.application;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

public class PluginOwner implements LifecycleOwner, ViewModelStoreOwner
{
    private final LifecycleRegistry _lifecycleRegistry = new LifecycleRegistry(this);
    private final ViewModelStore _viewModelStore = new ViewModelStore();

    @NonNull
    @Override
    public Lifecycle getLifecycle()
    {
        return _lifecycleRegistry;
    }

    @MainThread
    public void setCurrentState(@NonNull final Lifecycle.State state)
    {
        _lifecycleRegistry.setCurrentState(state);
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore()
    {
        return _viewModelStore;
    }
}
