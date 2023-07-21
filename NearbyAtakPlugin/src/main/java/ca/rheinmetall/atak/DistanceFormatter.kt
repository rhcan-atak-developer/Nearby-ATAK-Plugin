package ca.rheinmetall.atak

import com.atakmap.android.preference.UnitPreferences
import com.atakmap.coremap.conversions.Span
import com.atakmap.coremap.conversions.SpanUtilities
import javax.inject.Inject

class DistanceFormatter @Inject constructor(private val _unitPreferences: UnitPreferences) {
    fun format(meters: Double): String {
        return SpanUtilities.format(meters, Span.METER, _unitPreferences.getRangeUnits(meters), DECIMAL_POINTS)
    }

    companion object {
        private const val DECIMAL_POINTS = 2
    }
}