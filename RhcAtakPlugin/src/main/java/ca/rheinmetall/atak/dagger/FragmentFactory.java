package ca.rheinmetall.atak.dagger;

import androidx.fragment.app.Fragment;

public interface FragmentFactory
{
    <T extends Fragment> T instantiate(final Class<T> fragmentClass);

    void setFallbackClassLoader(ClassLoader fallbackClassLoader);
}
