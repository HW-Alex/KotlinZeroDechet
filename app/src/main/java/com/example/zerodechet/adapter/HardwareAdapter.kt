package com.example.zerodechet.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.example.zerodechet.Activities.HardwareDetailActivity
import com.example.zerodechet.Model.Hardware
import com.example.zerodechet.R
import com.example.zerodechet.Services.HardwareRowListener


class HardwareAdapter(context: Context, hardwareList: MutableList<Hardware>) : BaseAdapter() {

    private val _inflater: LayoutInflater = LayoutInflater.from(context)
    private var _hardwareList = hardwareList
    private var _rowListener: HardwareRowListener = context as HardwareRowListener

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val objectId: String = _hardwareList.get(position).objectId as String
        val title: String = _hardwareList.get(position).title as String
        val capacity: String?  = _hardwareList.get(position).capacity
        val frequency: String?  = _hardwareList.get(position).frequency
        val materialReference: String?  = _hardwareList.get(position).materialReference
        val processor: String?  = _hardwareList.get(position).processor
        val otherInformation: String?  = _hardwareList.get(position).otherInformation
        val type: String?  = _hardwareList.get(position).type
        val view: View
        val listRowHolder: ListRowHolder

        if (convertView == null) {
            view = _inflater.inflate(R.layout.hardware_rows, parent, false)
            listRowHolder = ListRowHolder(view)
            view.tag = listRowHolder
        } else {
            view = convertView
            listRowHolder = view.tag as ListRowHolder
        }
        listRowHolder.title.text = title
        listRowHolder.remove.setOnClickListener {
            _rowListener.onHardwareDelete(objectId)
        }
        listRowHolder.modify.setOnClickListener {
            _rowListener.onHardwareModify(it, objectId, title, type, capacity, frequency, materialReference, processor, otherInformation)
        }
        listRowHolder.title.setOnClickListener {
            val intent = Intent(it.context, HardwareDetailActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("capacity", capacity)
            intent.putExtra("frequency", frequency)
            intent.putExtra("materialReference", materialReference)
            intent.putExtra("processor", processor)
            intent.putExtra("type", type)
            intent.putExtra("otherInformation", otherInformation)
            // start your next activity
            startActivity(it.context, intent, null)
        }

        return view
    }

    override fun getItem(index: Int): Any {
        return _hardwareList[index]
    }

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    override fun getCount(): Int {
        return _hardwareList.size
    }

    private class ListRowHolder(row: View?) {
        val title: TextView = row!!.findViewById(R.id.txtHardwareTitle) as TextView
        val remove: ImageButton = row!!.findViewById(R.id.btnRemove) as ImageButton
        val modify: ImageButton = row!!.findViewById(R.id.btnModify) as ImageButton

    }
}