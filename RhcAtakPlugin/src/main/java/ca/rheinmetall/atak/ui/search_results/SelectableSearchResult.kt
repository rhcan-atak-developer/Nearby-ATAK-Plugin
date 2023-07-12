package ca.rheinmetall.atak.ui.search_results

import ca.rheinmetall.atak.json.PointOfInterestResult

data class SelectableSearchResult(val result: PointOfInterestResult, var selected: Boolean, val distance: Double)
