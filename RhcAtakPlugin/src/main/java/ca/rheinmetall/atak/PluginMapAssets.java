package ca.rheinmetall.atak;

import android.content.Context;
import android.net.Uri;

import com.atakmap.android.maps.assets.MapAssets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import ca.rheinmetall.atak.dagger.AtakContext;
import ca.rheinmetall.atak.dagger.PluginContext;

public class PluginMapAssets extends MapAssets {
    private final MapAssets _atakMapAssets;
    private final MapAssets _pluginMapAssets;
    private final String _prefix;

    public PluginMapAssets(@AtakContext Context atakContext, @PluginContext Context pluginContext, String prefix) {
        super(atakContext);
        this._atakMapAssets = new MapAssets(atakContext);
        this._pluginMapAssets = new MapAssets(pluginContext);
        this._prefix = prefix;
    }

    public InputStream getInputStream(Uri assetUri) throws IOException {
        try {
            Uri pluginUri = (Uri) Optional.ofNullable(this._prefix).map((prefix) -> {
                return Uri.parse(prefix + "/" + assetUri.toString());
            }).orElse(assetUri);
            InputStream inputStream = this._pluginMapAssets.getInputStream(pluginUri);
            return inputStream == null ? this._atakMapAssets.getInputStream(assetUri) : inputStream;
        } catch (IOException var4) {
            return this._atakMapAssets.getInputStream(assetUri);
        }
    }
}
