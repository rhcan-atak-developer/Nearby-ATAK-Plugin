package ca.rheinmetall.atak.model

import java.util.UUID

data class PointOfInterest(val lat: Double, val lon: Double, val pointOfInterestIcon: PointOfInterestIcon, val uuid: UUID = UUID.randomUUID(),) {

}