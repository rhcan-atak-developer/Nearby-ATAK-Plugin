package ca.rheinmetall.atak.json

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue


data class PointOfInterestResponseData (

  @JsonRawValue @JsonProperty("__copyright" ) var _copyright : String?            = null,
  @JsonRawValue @JsonProperty("results"     ) var results    : ArrayList<PointOfInterestResult> = arrayListOf()

)