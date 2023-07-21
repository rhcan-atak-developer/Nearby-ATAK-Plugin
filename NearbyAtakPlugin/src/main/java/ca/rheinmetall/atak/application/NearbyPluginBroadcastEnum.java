package ca.rheinmetall.atak.application;

import ca.rheinmetall.atak.broadcast.Broadcast;

public enum NearbyPluginBroadcastEnum implements Broadcast
{
    SHOW_POI_PLUGIN("ca.rheinmetall.atak.applicatio.SHOW_POI_PLUGIN"),
    SHOW_INCIDENTS_PLUGIN("ca.rheinmetall.atak.applicatio.SHOW_INCIDENTS_PLUGIN"),
    SHOW_SEARCH_RESULTS("ca.rheinmetall.atak.show_search_results"),
    CLOSE_SEARCH_RESULTS("ca.rheinmetall.atak.close_search_results");

    private final String _action;

    NearbyPluginBroadcastEnum(final String action)
    {
        _action = action;
    }

    @Override
    public String getAction()
    {
        return _action;
    }
}
