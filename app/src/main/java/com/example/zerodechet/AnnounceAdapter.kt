package com.example.zerodechet

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.squareup.picasso.Picasso
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
        val url: String? = _announceList.get(position).url.toString()
        val view: View
        val listRowHolder: ListRowHolder
        val urlDefault = "https://firebasestorage.googleapis.com/v0/b/techbytrash.appspot.com/o/Images_Pc%2Fdefault.png?alt=media&token=86e5c1e1-95b6-4ed7-bb28-a4c3697609fe"

        if (convertView == null) {
            view = _inflater.inflate(R.layout.announce_rows, parent, false)
            listRowHolder = ListRowHolder(view)
            view.tag = listRowHolder
        } else {
            view = convertView
            listRowHolder = view.tag as ListRowHolder
        }
        listRowHolder.title.text = title
        listRowHolder.price.text = price
        // Ici on test si une URL existe dans la base pour cet élément, si non on charge l'image par defaut.
        if(!_announceList.get(position).url.isNullOrEmpty()) {
            Picasso.get().load(_announceList.get(position).url.toString()).resize(220, 0).into(listRowHolder.picture)

        } else {
            Picasso.get().load(urlDefault).resize(220, 0).into(listRowHolder.picture)
        }
        Handler().postDelayed({
            listRowHolder.loadingSpinner.setVisibility(View.GONE)
        }, 1500)
        /*listRowHolder.reserved.setOnClickListener {
            _rowListener.onAnnounceChange(objectId, !reserved)
        }*/
        listRowHolder.remove.setOnClickListener {
            _rowListener.onAnnounceDelete(objectId)
        }
        listRowHolder.modify.setOnClickListener {
            _rowListener.onAnnounceModify(it, objectId, title, price, ram, hardDiskDrive, processor, screenWidth, otherComponents, url)
        }
        listRowHolder.picture.setOnClickListener {
            val intent = Intent(it.context, AnnounceDetailActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("price", price)
            intent.putExtra("ram", ram)
            intent.putExtra("hardDiskDrive", hardDiskDrive)
            intent.putExtra("processor", processor)
            intent.putExtra("screenWidth", screenWidth)
            intent.putExtra("otherComponents",otherComponents)
            if(!url.isNullOrEmpty()) {
                intent.putExtra("url", url)
            } else {
                intent.putExtra("url", urlDefault)

            }
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
        val price: TextView = row!!.findViewById(R.id.txtPrice) as TextView
        val remove: ImageButton = row!!.findViewById(R.id.btnRemove) as ImageButton
        val modify: ImageButton = row!!.findViewById(R.id.btnModify) as ImageButton
        val picture: ImageView = row!!.findViewById(R.id.imageView) as ImageView
        val loadingSpinner: ProgressBar = row!!.findViewById(R.id.progressBar)

    }
}