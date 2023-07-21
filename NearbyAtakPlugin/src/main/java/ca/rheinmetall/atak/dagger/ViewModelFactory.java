package ca.rheinmetall.atak.dagger;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory
{
    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> _creators;

    @Inject
    public ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators)
    {
        this._creators = creators;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull final Class<T> modelClass)
    {
        Provider<? extends ViewModel> creator = _creators.get(modelClass);
        if (creator == null)
        {
            for(Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : _creators.entrySet())
            {
                if (modelClass.isAssignableFrom(entry.getKey()))
                {
                    creator = entry.getValue();
                    break;
                }
            }
        }
        if (creator == null)
        {
            throw new IllegalArgumentException("unknown model class " + modelClass);
        }
        try
        {
            return (T) creator.get();
        }
        catch(final Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
