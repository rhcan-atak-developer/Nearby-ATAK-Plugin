package ca.rheinmetall.atak

import com.fasterxml.jackson.annotation.JsonProperty

class User {

    @JsonProperty("id")
    var id: Int? = null
    @JsonProperty("email")
    var email: String? = null
    @JsonProperty("first_name")
    var firstName: String? = null
    @JsonProperty("last_name")
    var lastName: String? = null
    @JsonProperty("avatar")
    var avatar: String? = null
}