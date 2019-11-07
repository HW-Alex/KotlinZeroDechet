package com.example.zerodechet

interface TaskRowListener {

        fun onTaskChange(objectId: String, isDone: Boolean)
        fun onTaskDelete(objectId: String)

}