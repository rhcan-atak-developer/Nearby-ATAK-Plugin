package ca.rheinmetall.atak.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.apache.commons.lang.ObjectUtils;

import javax.inject.Inject;

public abstract class PluginBroadcastReceiver extends BroadcastReceiver
{
    public abstract String getSupportedAction();
    protected abstract void onValidIntent(final Context context, final Intent intent);

    @Inject
    public void registerSelf(final PluginInstancesRepository pluginInstancesRepository)
    {
        pluginInstancesRepository.registerPluginBroadcastReceiver(this);
    }

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        if (ObjectUtils.equals(intent.getAction(), getSupportedAction()))
            onValidIntent(context, intent);
    }
}