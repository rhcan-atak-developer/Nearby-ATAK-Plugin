package ca.rheinmetall.atak.json

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue


data class PointOfInterestResult(
    @JsonRawValue @JsonProperty("__metadata") var metadata: Metadata? = Metadata(),
    @JsonRawValue @JsonProperty("EntityID") var entityID: String? = null,
    @JsonRawValue @JsonProperty("Name") var name: String? = null,
    @JsonRawValue @JsonProperty("DisplayName") var displayName: String? = null,
    @JsonRawValue @JsonProperty("Latitude") var latitude: Double? = null,
    @JsonRawValue @JsonProperty("Longitude") var longitude: Double? = null,
    @JsonRawValue @JsonProperty("AddressLine") var address: String? = null,
    @JsonRawValue @JsonProperty("Locality") var locality: String? = null,
    @JsonRawValue @JsonProperty("AdminDistrict2") var adminDistrict2: String? = null,
    @JsonRawValue @JsonProperty("AdminDistrict") var adminDistrict: String? = null,
    @JsonRawValue @JsonProperty("PostalCode") var postalCode: String? = null,
    @JsonRawValue @JsonProperty("CountryRegion") var countryRegion: String? = null,
    @JsonRawValue @JsonProperty("Phone") var phone: String? = null,
    @JsonRawValue @JsonProperty("EntityTypeID") var entityTypeID: Int? = null,
    @JsonRawValue @JsonProperty("SubEntityTypeID") var subEntityTypeID: Int? = null,
    @JsonRawValue @JsonProperty("__Distance") var distance: Double? = null
)