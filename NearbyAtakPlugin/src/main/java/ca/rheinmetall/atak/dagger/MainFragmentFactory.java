package ca.rheinmetall.atak.dagger;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Map;

import javax.inject.Inject;

public class MainFragmentFactory extends androidx.fragment.app.FragmentFactory implements FragmentFactory
{
    private final Map<Class<? extends Fragment>, androidx.fragment.app.FragmentFactory> _factories;
    private ClassLoader _fallbackClassLoader;

    @Inject
    public MainFragmentFactory(Map<Class<? extends Fragment>, androidx.fragment.app.FragmentFactory> factories)
    {
        _factories = factories;
    }

    @NonNull
    @Override
    public Fragment instantiate(@NonNull final ClassLoader classLoader, @NonNull final String className)
    {
        try
        {
            return doInstanciate(classLoader, className);
        }
        catch(Exception e)
        {
            if(_fallbackClassLoader != null)
                return doInstanciate(_fallbackClassLoader, className);
            else
                throw e;
        }
    }

    @NonNull
    private Fragment doInstanciate(final @NonNull ClassLoader classLoader, final @NonNull String className)
    {
        final Class<?> fragmentClass = loadFragmentClass(classLoader, className);
        final androidx.fragment.app.FragmentFactory factoryProvider = _factories.get(fragmentClass);

        if(factoryProvider != null)
        {
            return factoryProvider.instantiate(classLoader, className);
        }

        return super.instantiate(classLoader, className);
    }

    @Override
    public void setFallbackClassLoader(ClassLoader fallbackClassLoader)
    {
        _fallbackClassLoader = fallbackClassLoader;
    }

    public <T extends Fragment> T instantiate(final Class<T> fragmentClass)
    {
        final androidx.fragment.app.FragmentFactory factoryProvider = _factories.get(fragmentClass);

        final Fragment fragment;
        if(factoryProvider != null)
        {
            fragment = factoryProvider.instantiate(fragmentClass.getClassLoader(), fragmentClass.getName());
        }
        else
        {
            fragment = super.instantiate(fragmentClass.getClassLoader(), fragmentClass.getName());
        }

        return fragmentClass.cast(fragment);
    }
}