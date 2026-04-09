package com.example.list_temp.data

import java.util.UUID

data class Group(
    val id : UUID = UUID.randomUUID(),
    var name : String = "",
    var facultyID: UUID ?= null
)
