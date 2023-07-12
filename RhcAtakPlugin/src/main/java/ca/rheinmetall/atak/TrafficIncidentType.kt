package ca.rheinmetall.atak

enum class TrafficIncidentType(val typeCode: Int, val imageName: String = "traffic_jam.png") {
    Accident(1, "traffic_jam.png"),
    Congestion(2, "traffic_jam.png"),
    DisabledVehicle(3, "towing.ong"),
    MassTransit(4, "bus.png"),
    Miscellaneous(5),
    OtherNews(6),
    PlannedEvent(7),
    RoadHazard(8, "caution.png"),
    Construction(9, "road_works.png"),
    Alert(10, "caution.png"),
    Weather(11);

    companion object{
        fun fromCode(code: Int): TrafficIncidentType {
            for (value in TrafficIncidentType.values()) {
                if (value.typeCode == code) {
                    return value
                }
            }
            return TrafficIncidentType.Accident
        }
    }

}
