package ca.rheinmetall.atak.json

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue


data class PointOfInterestResponse (

  @JsonRawValue @JsonProperty("d" ) var pointOfInterestResponseData : PointOfInterestResponseData? = PointOfInterestResponseData()

)