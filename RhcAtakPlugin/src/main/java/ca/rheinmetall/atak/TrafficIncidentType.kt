package ca.rheinmetall.atak

enum class TrafficIncidentType(val typeCode: Int, val imageName: String = "traffic_jam.png", val isHidden : Boolean = false) {
    All(0),
    Accident(1, "traffic_jam.png"),
    Congestion(2, "traffic_jam.png"),
    DisabledVehicle(3, "towing.png"),
    MassTransit(4, "bus.png"),
    Miscellaneous(5, "traffic_jam.png", true),
    OtherNews(6, "traffic_jam.png", true),
    PlannedEvent(7, "traffic_jam.png", true),
    RoadHazard(8, "caution.png"),
    Construction(9, "road_works.png"),
    Alert(10, "caution.png"),
    Weather(11, "traffic_jam.png", true);

    companion object{
        fun fromCode(code: Int): TrafficIncidentType {
            for (value in TrafficIncidentType.values()) {
                if (value.typeCode == code) {
                    return value
                }
            }
            return Accident
        }
    }

}
