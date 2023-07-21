package ca.rheinmetall.atak.json.route

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue


data class TrafficIncidentResponseData (
  @JsonRawValue @JsonProperty("estimatedTotal" ) var estimatedTotal : Int?                 = null,
  @JsonRawValue @JsonProperty("resources"      ) var resources      : ArrayList<TrafficIncidentResult> = arrayListOf()
)