package com.example.zerodechet

class Hardware {
    companion object Factory {
        fun create(): Hardware = Hardware()
    }

    var objectId: String? = null
    var title: Double? = null
    var capacity: String? = null
    var frequency: String? = null
    var processor: String? = null
    var material: String? = null
    var otherInformation: String? = null

}