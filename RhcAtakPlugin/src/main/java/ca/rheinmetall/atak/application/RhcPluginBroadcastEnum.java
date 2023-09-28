package ca.rheinmetall.atak.application;

import ca.rheinmetall.atak.broadcast.Broadcast;

public enum RhcPluginBroadcastEnum implements Broadcast
{
    SHOW_RHC_PLUGIN("ca.rheinmetall.atak.applicatio.SHOW_RHC_PLUGIN");

    private final String _action;

    RhcPluginBroadcastEnum(final String action)
    {
        _action = action;
    }

    @Override
    public String getAction()
    {
        return _action;
    }
}
