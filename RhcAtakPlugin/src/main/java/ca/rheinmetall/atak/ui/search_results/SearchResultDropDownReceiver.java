package ca.rheinmetall.atak.ui.search_results;

import android.content.Context;
import android.content.Intent;

import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapView;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.rheinmetall.atak.application.RhcPluginBroadcastEnum;
import ca.rheinmetall.atak.dagger.FragmentFactory;
import ca.rheinmetall.atak.ui.PointOfInterestFragment;

@Singleton
public class SearchResultDropDownReceiver extends DropDownReceiver
{
    private final FragmentFactory _fragmentFactory;

    @Inject
    public SearchResultDropDownReceiver(final MapView mapView, final FragmentFactory fragmentFactory)
    {
        super(mapView);
        _fragmentFactory = fragmentFactory;
        setRetain(false);
    }

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        if(RhcPluginBroadcastEnum.SHOW_SEARCH_RESULTS.getAction().equals(intent.getAction()))
        {
            SearchResultFragment fragment = _fragmentFactory.instantiate(SearchResultFragment.class);
            showDropDown(fragment, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH, HALF_HEIGHT);
        }
        else if (RhcPluginBroadcastEnum.CLOSE_SEARCH_RESULTS.getAction().equals(intent.getAction()))
        {
            closeDropDown();
        }
    }

    @Override
    protected void disposeImpl()
    {
    }
}
