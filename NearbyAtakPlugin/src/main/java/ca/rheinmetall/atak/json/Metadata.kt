package ca.rheinmetall.atak.json

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue


data class Metadata(
    @JsonRawValue @JsonProperty("uri") var uri: String? = null
)