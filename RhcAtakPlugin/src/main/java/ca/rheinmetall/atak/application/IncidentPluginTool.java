package ca.rheinmetall.atak.application;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.atak.plugins.impl.AbstractPluginTool;

import ca.rheinmetall.atak.R;

public class IncidentPluginTool extends AbstractPluginTool
{
    public IncidentPluginTool(final Context context)
    {
        super(context,
              context.getString(R.string.incidents),
              context.getString(R.string.incidents),
              ContextCompat.getDrawable(context, R.drawable.incident_plugin),
              RhcPluginBroadcastEnum.SHOW_INCIDENTS_PLUGIN.getAction());
    }
}
