package ca.rheinmetall.atak.json.route

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue

data class Location (
  @JsonRawValue @JsonProperty("type"        ) var type        : String?           = null,
  @JsonRawValue @JsonProperty("coordinates" ) var coordinates : ArrayList<Double> = arrayListOf()
)