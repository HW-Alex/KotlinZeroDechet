package com.example.zerodechet.Services

import android.view.View

interface AnnounceRowListener {

        fun onAnnounceChange(objectId: String, isReserved: Boolean)
        fun onAnnounceDelete(objectId: String)
        fun onAnnounceModify(view: View, objectId: String, title: String, price: String?, ram: String?, hardDiskDrive: String?, processor: String?, screenWidth: String?, otherComponents: String?, url: String? )

}