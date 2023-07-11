package com.example.example

import ca.rheinmetall.atak.json.route.TrafficIncident
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue


data class TrafficIncidentResponseData (
  @JsonRawValue @JsonProperty("estimatedTotal" ) var estimatedTotal : Int?                 = null,
  @JsonRawValue @JsonProperty("resources"      ) var resources      : ArrayList<TrafficIncident> = arrayListOf()
)