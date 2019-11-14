package com.example.zerodechet

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.announce_rows.*


class AnnounceAdapter(context: Context, announceList: MutableList<Announce>) : BaseAdapter() {

    private val _inflater: LayoutInflater = LayoutInflater.from(context)
    private var _announceList = announceList
    private var _rowListener: AnnounceRowListener = context as AnnounceRowListener

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val objectId: String = _announceList.get(position).objectId as String
        val title: String = _announceList.get(position).title as String
        val price: String?  = _announceList.get(position).price
        val ram: String?  = _announceList.get(position).ram
        val hardDiskDrive: String?  = _announceList.get(position).hardDiskDrive
        val processor: String?  = _announceList.get(position).processor
        val screenWidth: String?  = _announceList.get(position).screenWidth
        val otherComponents: String?  = _announceList.get(position).otherComponents
        val reserved: Boolean = _announceList.get(position).reserved as Boolean

        val view: View
        val listRowHolder: ListRowHolder

        if (convertView == null) {
            view = _inflater.inflate(R.layout.announce_rows, parent, false)
            listRowHolder = ListRowHolder(view)
            view.tag = listRowHolder
        } else {
            view = convertView
            listRowHolder = view.tag as ListRowHolder
        }

        listRowHolder.title.text = title
        listRowHolder.reserved.isChecked = reserved
        listRowHolder.reserved.setOnClickListener {
            _rowListener.onAnnounceChange(objectId, !reserved)
        }
        listRowHolder.remove.setOnClickListener {
            _rowListener.onAnnounceDelete(objectId)
        }
        listRowHolder.modify.setOnClickListener {
            _rowListener.onAnnounceModify(it, objectId, title, price, ram, hardDiskDrive, processor, screenWidth, otherComponents)
        }
        listRowHolder.title.setOnClickListener {
            val intent = Intent(it.context, AnnounceDetailActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("price", price)
            intent.putExtra("ram", ram)
            intent.putExtra("hardDiskDrive", hardDiskDrive)
            intent.putExtra("processor", processor)
            intent.putExtra("screenWidth", screenWidth)
            intent.putExtra("otherComponents",otherComponents)
            // start your next activity
            startActivity(it.context, intent, null)
        }

        return view
    }

    override fun getItem(index: Int): Any {
        return _announceList[index]
    }

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    override fun getCount(): Int {
        return _announceList.size
    }

    private class ListRowHolder(row: View?) {
        val title: TextView = row!!.findViewById(R.id.txtAnnounceTitle) as TextView
        val reserved: CheckBox = row!!.findViewById(R.id.chkDone) as CheckBox
        val remove: ImageButton = row!!.findViewById(R.id.btnRemove) as ImageButton
        val modify: ImageButton = row!!.findViewById(R.id.btnModify) as ImageButton
    }
}