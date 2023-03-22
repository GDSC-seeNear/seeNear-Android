package com.kgg.android.seenear.network.data

import java.sql.Date
import java.util.*

data class registerResponse (
        var id: Int ?= 0,
        var phoneNumber: String ?= null,
        var name: String ?= null,
        var birth: String?= null,
        var addressLati: Double ?= 0.0,
        var addressLongi: Double ?= 0.0,
        var addressDetail: String ?= null,
        var isConnect: Boolean ?= false,
        var guardianId : Int? = 0,
        var emergencyPhoneNumber : List<String>? = null
        )