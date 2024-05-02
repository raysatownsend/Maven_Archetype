package com.supernova.data.vo.v2

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.util.*

@JsonPropertyOrder("id", "lastName", "firstName", "gender", "address", "birthday")

data class ClientVO(
    var id: Long = 0,

    @field:JsonProperty("first_Name")
    var firstName: String = "",

    @field:JsonProperty("last_Name")
    var lastName: String = "",

    var address: String = "",
    var gender: String = "",

    @field:JsonIgnore
    var birthDay: Date? = null
)
