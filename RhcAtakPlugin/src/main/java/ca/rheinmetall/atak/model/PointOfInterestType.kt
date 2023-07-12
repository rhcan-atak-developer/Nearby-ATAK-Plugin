package ca.rheinmetall.atak.model

import ca.rheinmetall.atak.R

enum class PointOfInterestType(val ids: List<Int>, val stringRes: Int, val imageName: String = "bank.png") {
    AIRPORT(listOf(4581), R.string.airport),
    BANK(listOf(6000), R.string.bank, "bank.png"),
    BORDER_POST(listOf(9999), R.string.border_post),
    BRIDGE(listOf(19), R.string.bridge),
    BUS_STATION(listOf(4170), R.string.bus_station),
    CITY_HALL(listOf(9121), R.string.city_hall),
    COMMUNITY_CENTER(listOf(7994), R.string.community_center),
    CONVENTION_CENTER(listOf(7990), R.string.convention_center),
    COURT_HOUSE(listOf(9211), R.string.court_house),
    EMBASSY(listOf(9993), R.string.embassy),
    FERRY_TERMINAL(listOf(4482), R.string.ferry_terminal),
    FIRE_STATION(listOf(270), R.string.fire_station),
    GAS_STATION(listOf(5540), R.string.gas_station),
    GOVERNMENT_STRUCTURE(listOf(9525), R.string.gouvernment_structure),
    HELIPORT(listOf(73), R.string.heliport),
    HOSPITAL(listOf(8060), R.string.hospital, "hospital.png"),
    MARINA(listOf(4493), R.string.marina),
    MEDICAL_SERVICE(listOf(9583), R.string.medical_service),
    MILITARY_BASE(listOf(9715), R.string.military_base),
    POLICE_STATION(listOf(9221), R.string.police_station),
    PORT(listOf(280), R.string.port),
    POST_OFFICE(listOf(9530), R.string.post_office),
    PRISON(listOf(137), R.string.prison),
    RAILWAY_STATION(listOf(4013), R.string.railway_station),
    SCHOOL(listOf(8211), R.string.school),
    STADIUM(listOf(7997), R.string.stadium),
    TRANSPORTATION_SERVICE(listOf(7999, 9593), R.string.transportation_service),
    TRUCK_STOP(listOf(9522), R.string.truck_stop),
    VETERINARIAN(listOf(304), R.string.veterinarian)
}