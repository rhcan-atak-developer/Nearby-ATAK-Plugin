package ca.rheinmetall.atak.application;

import com.atak.plugins.impl.AbstractPluginTool;

import android.content.Context;
import androidx.core.content.ContextCompat;

import ca.rheinmetall.atak.R;

public class PoiPluginTool extends AbstractPluginTool
{
    public PoiPluginTool(final Context context)
    {
        super(context,
              context.getString(R.string.points_of_interest),
              context.getString(R.string.points_of_interest),
              ContextCompat.getDrawable(context, R.drawable.ic_launcher),
              RhcPluginBroadcastEnum.SHOW_POI_PLUGIN.getAction());
    }
}
