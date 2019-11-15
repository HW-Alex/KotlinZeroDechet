package com.example.zerodechet

import android.view.View

interface HardwareRowListener {

        fun onHardwareChange(objectId: String, isReserved: Boolean)
        fun onHardwareDelete(objectId: String)
        fun onHardwareModify(view: View, objectId: String, title: String, type: String?, capacity: String?, frequency: String?, materialReference: String?, processor: String?, otherInformation: String? )

}