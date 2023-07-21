package ca.rheinmetall.atak.dagger;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import javax.inject.Provider;

public class DaggerFragmentFactory<T extends Fragment> extends FragmentFactory
{
    private final Provider<T> _provider;
    private final Class<T> _clazz;

    public DaggerFragmentFactory(final Provider<T> provider, final Class<T> clazz)
    {
        _provider = provider;
        _clazz = clazz;
    }

    @NonNull
    @Override
    public Fragment instantiate(@NonNull final ClassLoader classLoader, @NonNull final String className)
    {
        final Class<? extends Fragment> fragmentClass = loadFragmentClass(classLoader, className);

        //noinspection ObjectEquality
        if (fragmentClass == _clazz)
        {
            return _provider.get();
        }

        return super.instantiate(classLoader, className);
    }
}