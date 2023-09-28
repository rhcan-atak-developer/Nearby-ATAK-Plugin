package ca.rheinmetall.atak.application;

import com.atak.plugins.impl.AbstractPluginTool;

import android.content.Context;
import androidx.core.content.ContextCompat;

import ca.rheinmetall.atak.R;

public class RhcPluginTool extends AbstractPluginTool
{
    public RhcPluginTool(final Context context)
    {
        super(context,
              context.getString(R.string.app_name),
              context.getString(R.string.app_name),
              ContextCompat.getDrawable(context, R.drawable.ic_launcher),
              RhcPluginBroadcastEnum.SHOW_RHC_PLUGIN.getAction());
    }
}
