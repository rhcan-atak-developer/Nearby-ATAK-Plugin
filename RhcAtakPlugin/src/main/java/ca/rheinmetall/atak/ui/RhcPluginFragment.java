package ca.rheinmetall.atak.ui;

import javax.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ca.rheinmetall.atak.dagger.PluginContext;
import ca.rheinmetall.atak.databinding.RhcPluginFragmentBinding;

public class RhcPluginFragment extends Fragment
{
    private final Context _pluginContext;

    private RhcPluginFragmentBinding _binding;

    @Inject
    public RhcPluginFragment(@PluginContext final Context pluginContext)
    {
        _pluginContext = pluginContext;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        _binding = RhcPluginFragmentBinding.inflate(LayoutInflater.from(_pluginContext));
        return _binding.getRoot();
    }
}
