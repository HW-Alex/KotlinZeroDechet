package com.example.zerodechet.Model

class Announce {
    companion object Factory {
        fun create(): Announce =
            Announce()
    }

    var objectId: String? = null
    var title: String? = null
    var price: String? = null
    var ram: String? = null
    var hardDiskDrive: String? = null
    var processor: String? = null
    var screenWidth: String? = null
    var otherComponents: String? = null
    var reserved: Boolean? = false
    var url: String? = null
}