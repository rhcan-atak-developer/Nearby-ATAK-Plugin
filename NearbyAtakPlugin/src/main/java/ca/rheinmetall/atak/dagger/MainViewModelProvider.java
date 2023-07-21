package ca.rheinmetall.atak.dagger;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import javax.inject.Inject;

public class MainViewModelProvider
{
    private final ViewModelFactory _viewModelFactory;

    @Inject
    public MainViewModelProvider(final ViewModelFactory viewModelFactory)
    {
        _viewModelFactory = viewModelFactory;
    }

    public <T extends ViewModel> T get(final ViewModelStoreOwner owner, @NonNull final Class<T> modelClass)
    {
        return new ViewModelProvider(owner, _viewModelFactory).get(modelClass);
    }
}