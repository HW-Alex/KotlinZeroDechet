package com.example.zerodechet

class Hardware {
    companion object Factory {
        fun create(): Hardware = Hardware()
    }

    var objectId: String? = null
    var title: String? = null
    var type: String? = null
    var capacity: String? = null
    var frequency: String? = null
    var processor: String? = null
    var materialReference: String? = null
    var otherInformation: String? = null

}