package ca.rheinmetall.atak.json.route

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue

data class TrafficIncidentResult(
    @JsonRawValue @JsonProperty("__type") var _type: String? = null,
    @JsonRawValue @JsonProperty("point") var point: Location? = Location(),
    @JsonRawValue @JsonProperty("alertCCodes") var alertCCodes: ArrayList<Int> = arrayListOf(),
    @JsonRawValue @JsonProperty("delay") var delay: Int? = null,
    @JsonRawValue @JsonProperty("description") var description: String? = null,
    @JsonRawValue @JsonProperty("end") var end: String? = null,
    @JsonRawValue @JsonProperty("eventList") var eventList: ArrayList<Int> = arrayListOf(),
    @JsonRawValue @JsonProperty("icon") var icon: Int? = null,
    @JsonRawValue @JsonProperty("incidentId") var incidentId: String? = null,
    @JsonRawValue @JsonProperty("isEndTimeBackfilled") var isEndTimeBackfilled: Boolean? = null,
    @JsonRawValue @JsonProperty("isJamcident") var isJamcident: Boolean? = null,
    @JsonRawValue @JsonProperty("lastModified") var lastModified: String? = null,
    @JsonRawValue @JsonProperty("roadClosed") var roadClosed: Boolean? = null,
    @JsonRawValue @JsonProperty("severity") var severity: Int? = null,
    @JsonRawValue @JsonProperty("severityScore") var severityScore: Int? = null,
    @JsonRawValue @JsonProperty("source") var source: Int? = null,
    @JsonRawValue @JsonProperty("start") var start: String? = null,
    @JsonRawValue @JsonProperty("title") var title: String? = null,
    @JsonRawValue @JsonProperty("toPoint") var location: Location? = Location(),
    @JsonRawValue @JsonProperty("type") var type: Int? = null,
    @JsonRawValue @JsonProperty("verified") var verified: Boolean? = null
)