package com.example.zerodechet.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.zerodechet.Model.Hardware
import com.example.zerodechet.Model.Statics
import com.example.zerodechet.R
import com.example.zerodechet.Services.HardwareRowListener
import com.example.zerodechet.adapter.HardwareAdapter
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_hardware.*
import kotlinx.android.synthetic.main.content_hardware.*


class HardwareActivity : AppCompatActivity(), HardwareRowListener {

    lateinit var _db: DatabaseReference
    lateinit var _adapter: HardwareAdapter
    lateinit var _imageHardware: ImageView
    var _hardwareList: MutableList<Hardware>? = null
    var _hardwareListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            loadHardwareList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }
    val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hardware)
        _db = FirebaseDatabase.getInstance().reference
        _hardwareList = mutableListOf()
        _adapter = HardwareAdapter(this, _hardwareList!!)
        val listHardwares = findViewById<ListView>(R.id.listviewHardware)
        listHardwares.adapter = _adapter
        listviewHardware!!.setAdapter(_adapter)
        btnNewHardware.setOnClickListener { view ->
            newHardwareForm(view)
        }
        _db.orderByKey().addValueEventListener(_hardwareListener)
    }

    override fun onHardwareChange(objectId: String, isReserved: Boolean) {
        val hardware = _db.child(Statics.FIREBASE_HARDWARE).child(objectId).child("reserved")
        hardware.setValue(isReserved)
    }
    override fun onHardwareDelete(objectId: String) {
        val hardware = _db.child(Statics.FIREBASE_HARDWARE).child(objectId)
        hardware.removeValue()
    }
    override fun onHardwareModify(view: View, objectId: String, title: String, type: String?,
                                  capacity: String?, frequency: String?, materialReference: String?,
                                  processor: String?, otherInformation: String? ) {
        modifyHardwareForm(view, objectId, title, type, capacity, frequency,
            materialReference, processor, otherInformation)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    @SuppressLint("RestrictedApi")

    fun newHardwareForm(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ajouter une nouvelle Annonce")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_hardware, null)
        val txtTitle  = dialogLayout.findViewById<EditText>(R.id.txtTitle)
        val txtPrice  = dialogLayout.findViewById<EditText>(R.id.txtPrice)
        val txtRam  = dialogLayout.findViewById<EditText>(R.id.txtRam)
        val txtHardDiskDrive  = dialogLayout.findViewById<EditText>(R.id.txtHardDiskDrive)
        val txtProcessor  = dialogLayout.findViewById<EditText>(R.id.txtProcessor)
        val txtScreenWidth  = dialogLayout.findViewById<EditText>(R.id.txtScreenWidth)
        val txtOther  = dialogLayout.findViewById<EditText>(R.id.txtOther)
        _imageHardware = dialogLayout.findViewById(R.id.imageView)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Ajouter") {
                dialogInterface, i -> addHardware(txtTitle.text.toString(),txtPrice.text.toString(),
            txtRam.text.toString(),txtHardDiskDrive.text.toString(),txtProcessor.text.toString(),
            txtScreenWidth.text.toString(),txtOther.text.toString())
        }
        builder.show()
    }

    fun modifyHardwareForm(view: View, objectId: String, title: String, type: String?,
                           capacity: String?, frequency: String?, materialReference: String?,
                           processor: String?, otherInformation: String?) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Modifier l'Annonce")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_hardware, null)
        dialogLayout.findViewById<EditText>(R.id.txtTitle).setText(title)
        dialogLayout.findViewById<EditText>(R.id.txtType).setText(type)
        dialogLayout.findViewById<EditText>(R.id.txtCapacity).setText(capacity)
        dialogLayout.findViewById<EditText>(R.id.txtFrequency).setText(frequency)
        dialogLayout.findViewById<EditText>(R.id.txtMaterialReference).setText(materialReference)
        dialogLayout.findViewById<EditText>(R.id.txtProcessor).setText(processor)
        dialogLayout.findViewById<EditText>(R.id.txtOtherInformation).setText(otherInformation)
        val txtTitle  = dialogLayout.findViewById<EditText>(R.id.txtTitle).text
        val txtType  = dialogLayout.findViewById<EditText>(R.id.txtPrice).text
        val txtCapacity  = dialogLayout.findViewById<EditText>(R.id.txtRam).text
        val txtFrequency  = dialogLayout.findViewById<EditText>(R.id.txtHardDiskDrive).text
        val txtMaterialReferece  = dialogLayout.findViewById<EditText>(R.id.txtProcessor).text
        val txtProcessor  = dialogLayout.findViewById<EditText>(R.id.txtScreenWidth).text
        val txtOtherInformation  = dialogLayout.findViewById<EditText>(R.id.txtOther).text
        builder.setView(dialogLayout)
        builder.setPositiveButton("Modifier") { dialogInterface, i ->
            modifyHardware(
                objectId, txtTitle.toString(), txtType.toString(),
                txtCapacity.toString(), txtFrequency.toString(), txtMaterialReferece.toString(),
                txtProcessor.toString(), txtOtherInformation.toString()
            )
        }
        builder.show()
    }

    private fun addHardware(title: String, type: String?, capacity: String?, frequency: String?, materialReference: String?, processor: String?, otherInformation: String?){
        //Declare and Initialise the hardware
        val hardware = Hardware.create()
        //Set hardware Description and isReserved Status
        hardware.title = title
        hardware.type = type
        hardware.capacity = capacity
        hardware.frequency = frequency
        hardware.processor = processor
        hardware.materialReference = materialReference
        hardware.otherInformation = otherInformation
        //Get the object id for the new hardware from the Firebase Database
        val newHardware = _db.child(Statics.FIREBASE_HARDWARE).push()
        hardware.objectId = newHardware.key

        //Set the values for new hardware in the firebase using the footer form
        newHardware.setValue(hardware)
        //Reset the new hardware description field for reuse.
        txtNewHardwareTitle.setText("")
        Toast.makeText(this, "Annonce ajoutée : " + hardware.title, Toast.LENGTH_SHORT).show()
    }

    private fun modifyHardware(objectId: String, title: String, type: String?, capacity: String?, frequency: String?, materialReference: String?, processor: String?, otherInformation: String?){
        //Declare and Initialise the hardware
        val hardware = _db.child(Statics.FIREBASE_HARDWARE).child(objectId)
        hardware.child("title").setValue(title)
        hardware.child("type").setValue(type)
        hardware.child("capacity").setValue(capacity)
        hardware.child("frequency").setValue(frequency)
        hardware.child("processor").setValue(processor)
        hardware.child("materialReference").setValue(materialReference)
        hardware.child("otherInformation").setValue(otherInformation)
        Toast.makeText(this, "Annonce modifiée : " + title, Toast.LENGTH_SHORT).show()
    }

    private fun loadHardwareList(dataSnapshot: DataSnapshot) {
        Log.d("MainActivity", "loadHardwareList")

        val hardwares = dataSnapshot.children.iterator()

        //Check if current database contains any collection
        if (hardwares.hasNext()) {

            _hardwareList!!.clear()


            val listIndex = hardwares.next()
            val itemsIterator = listIndex.children.iterator()

            //check if the collection has any hardware or not
            while (itemsIterator.hasNext()) {

                //get current hardware
                val currentItem = itemsIterator.next()
                val hardware = Hardware.create()

                //get current data in a map
                val map = currentItem.getValue() as HashMap<String, Any>;

                //key will return the Firebase ID
                hardware.objectId = currentItem.key
                hardware.capacity = map.get("capacity") as String?
                hardware.title = map.get("title") as String?
                hardware.frequency = map.get("frequency") as String?
                hardware.type = map.get("type") as String?
                hardware.otherInformation = map.get("otherInformation") as String?
                hardware.processor = map.get("processor") as String?
                _hardwareList!!.add(hardware)
            }
        } else {
            _hardwareList!!.clear()
        }

        //alert adapter that has changed
        _adapter.notifyDataSetChanged()
    }
}