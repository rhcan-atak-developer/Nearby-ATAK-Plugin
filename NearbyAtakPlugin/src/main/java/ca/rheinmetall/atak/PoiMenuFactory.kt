package ca.rheinmetall.atak

import android.content.Context
import android.content.Intent
import android.net.Uri
import ca.rheinmetall.atak.dagger.AtakContext
import ca.rheinmetall.atak.dagger.PluginContext
import com.atakmap.android.maps.MapDataRef
import com.atakmap.android.maps.MapItem
import com.atakmap.android.menu.MapMenuButtonWidget
import com.atakmap.android.menu.MapMenuFactory
import com.atakmap.android.menu.MapMenuWidget
import com.atakmap.android.menu.MenuResourceFactory
import com.atakmap.android.menu.PluginMenuParser
import com.atakmap.android.widgets.WidgetIcon
import gov.tak.api.commons.graphics.IIcon
import javax.inject.Inject

class PoiMenuFactory @Inject constructor(
    @PluginContext private val pluginContext: Context,
    @AtakContext private val atakContext: Context,
    private val menuResourceFactory: MenuResourceFactory
) : MapMenuFactory {
    override fun create(mapItem: MapItem): MapMenuWidget?{
        if (mapItem.getMetaString("POI_PHONE_NUMBER", null) != null) {
            val menu = menuResourceFactory.create(mapItem)
            val mapMenuWidgetIcon = createWidgetIcon(pluginContext, "assets/phone-custom.png")
            val widget = MapMenuButtonWidget(pluginContext)
            widget.widgetIcon = mapMenuWidgetIcon
            widget.setOnClickAction { _, _ -> run {
                val phoneNumber = mapItem.getMetaString("POI_PHONE_NUMBER", null)
                val intent = Intent()
                intent.data = Uri.parse("tel:${phoneNumber}");
                atakContext.startActivity(intent); }
            }
            menu.addChildWidget(widget)
            return menu
        }
        return null
    }

    private fun createWidgetIcon(pluginContext: Context, path: String): IIcon? {
        val asset = PluginMenuParser.getItem(pluginContext, path)
        val mapDataRef = MapDataRef.parseUri(asset)
        val builder = WidgetIcon.Builder()
        return builder.setImageRef(0, mapDataRef)
            .setAnchor(16, 16)
            .setSize(32, 32)
            .build()
    }
}