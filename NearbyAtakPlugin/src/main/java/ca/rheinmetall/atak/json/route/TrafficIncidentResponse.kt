package ca.rheinmetall.atak.json.route

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue


data class TrafficIncidentResponse (
  @JsonRawValue @JsonProperty("authenticationResultCode" ) var authenticationResultCode : String?                 = null,
  @JsonRawValue @JsonProperty("brandLogoUri"             ) var brandLogoUri             : String?                 = null,
  @JsonRawValue @JsonProperty("copyright"                ) var copyright                : String?                 = null,
  @JsonRawValue @JsonProperty("resourceSets"             ) var trafficIncidentResponseData : ArrayList<TrafficIncidentResponseData> = arrayListOf(),
  @JsonRawValue @JsonProperty("statusCode"               ) var statusCode               : Int?                    = null,
  @JsonRawValue @JsonProperty("statusDescription"        ) var statusDescription        : String?                 = null,
  @JsonRawValue @JsonProperty("traceId"                  ) var traceId                  : String?                 = null
)