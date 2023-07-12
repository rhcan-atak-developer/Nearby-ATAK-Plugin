package ca.rheinmetall.atak

enum class TrafficIncidentType(val typeCode: Int) {
    Accident(1),
    Congestion(2),
    DisabledVehicle(3),
    MassTransit(4),
    Miscellaneous(5),
    OtherNews(6),
    PlannedEvent(7),
    RoadHazard(8),
    Construction(9),
    Alert(10),
    Weather(11)
}
