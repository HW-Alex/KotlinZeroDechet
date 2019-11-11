package com.example.zerodechet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_dialog_announce.*
import kotlinx.android.synthetic.main.announce_rows.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), AnnounceRowListener {

    lateinit var _db: DatabaseReference
    lateinit var _adapter: AnnounceAdapter
    var _announceList: MutableList<Announce>? = null
    var _fbAuth = FirebaseAuth.getInstance()
    var _announceListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            loadAnnounceList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }

    override fun onAnnounceChange(objectId: String, isReserved: Boolean) {
        val announce = _db.child(Statics.FIREBASE_ANNOUNCE).child(objectId).child("reserved")
        announce.setValue(isReserved)
    }
    override fun onAnnounceDelete(objectId: String) {
        val announce = _db.child(Statics.FIREBASE_ANNOUNCE).child(objectId)
        announce.removeValue()
    }
    override fun onAnnounceModify(view: View, objectId: String, title: String, price: String?,
                                  ram: String?, hardDiskDrive: String?, processor: String?,
                                  screenWidth: String?, otherComponents: String? ) {
        modifyAnnounceForm(view, objectId, title, price, ram, hardDiskDrive,
            processor, screenWidth, otherComponents)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        var btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {view ->
            signIn(view,"alex@ynov.com", "mdpmdp")
        }
        _db = FirebaseDatabase.getInstance().reference
        _announceList = mutableListOf()
        _adapter = AnnounceAdapter(this, _announceList!!)
        listviewAnnounce!!.setAdapter(_adapter)
        btnNewAnnounce.setOnClickListener { view ->
            newAnnounceForm(view)
        }
        _db.orderByKey().addValueEventListener(_announceListener)
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

    fun newAnnounceForm(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ajouter une nouvelle Annonce")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_announce, null)
        val txtTitle  = dialogLayout.findViewById<EditText>(R.id.txtTitle)
        val txtPrice  = dialogLayout.findViewById<EditText>(R.id.txtPrice)
        val txtRam  = dialogLayout.findViewById<EditText>(R.id.txtRam)
        val txtHardDiskDrive  = dialogLayout.findViewById<EditText>(R.id.txtHardDiskDrive)
        val txtProcessor  = dialogLayout.findViewById<EditText>(R.id.txtProcessor)
        val txtScreenWidth  = dialogLayout.findViewById<EditText>(R.id.txtScreenWidth)
        val txtOther  = dialogLayout.findViewById<EditText>(R.id.txtOther)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Ajouter") {
            dialogInterface, i -> addAnnounce(txtTitle.text.toString(),txtPrice.text.toString(),
            txtRam.text.toString(),txtHardDiskDrive.text.toString(),txtProcessor.text.toString(),
            txtScreenWidth.text.toString(),txtOther.text.toString())
        }
        builder.show()
    }

    fun modifyAnnounceForm(view: View, objectId: String, title: String, price: String?, ram: String?, hardDiskDrive: String?, processor: String?, screenWidth: String?, otherComponents: String?) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Modifier l'Annonce")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_announce, null)
        dialogLayout.findViewById<EditText>(R.id.txtTitle).setText(title)
        dialogLayout.findViewById<EditText>(R.id.txtPrice).setText(price)
        dialogLayout.findViewById<EditText>(R.id.txtRam).setText(ram)
        dialogLayout.findViewById<EditText>(R.id.txtHardDiskDrive).setText(hardDiskDrive)
        dialogLayout.findViewById<EditText>(R.id.txtProcessor).setText(processor)
        dialogLayout.findViewById<EditText>(R.id.txtScreenWidth).setText(screenWidth)
        dialogLayout.findViewById<EditText>(R.id.txtOther).setText(otherComponents)
        val txtTitle  = dialogLayout.findViewById<EditText>(R.id.txtTitle).text
        val txtPrice  = dialogLayout.findViewById<EditText>(R.id.txtPrice).text
        val txtRam  = dialogLayout.findViewById<EditText>(R.id.txtRam).text
        val txtHardDiskDrive  = dialogLayout.findViewById<EditText>(R.id.txtHardDiskDrive).text
        val txtProcessor  = dialogLayout.findViewById<EditText>(R.id.txtProcessor).text
        val txtScreenWidth  = dialogLayout.findViewById<EditText>(R.id.txtScreenWidth).text
        val txtOther  = dialogLayout.findViewById<EditText>(R.id.txtOther).text
        builder.setView(dialogLayout)
        builder.setPositiveButton("Modifier") {
            dialogInterface, i -> modifyAnnounce(objectId, txtTitle.toString(),txtPrice.toString(),
            txtRam.toString(),txtHardDiskDrive.toString(),txtProcessor.toString(),
            txtScreenWidth.toString(),txtOther.toString())
        }
        builder.show()
    }

    private fun addAnnounce(title: String, price: String, ram: String, hardDiskDrive: String, processor: String, screen: String, other: String){
        //Declare and Initialise the announce
        val announce = Announce.create()
        //Set announce Description and isReserved Status
        announce.title = title
        announce.price = price
        announce.ram = ram
        announce.hardDiskDrive = hardDiskDrive
        announce.processor = processor
        announce.screenWidth = screen
        announce.otherComponents = other
        announce.reserved = false

        //Get the object id for the new announce from the Firebase Database
        val newAnnounce = _db.child(Statics.FIREBASE_ANNOUNCE).push()
        announce.objectId = newAnnounce.key

        //Set the values for new announce in the firebase using the footer form
        newAnnounce.setValue(announce)
        //Reset the new announce description field for reuse.
        txtNewAnnounceTitle.setText("")
        Toast.makeText(this, "Annonce ajoutée : " + announce.title, Toast.LENGTH_SHORT).show()
    }

    private fun modifyAnnounce(objectId: String, title: String, price: String, ram: String, hardDiskDrive: String, processor: String, screen: String, other: String){
        //Declare and Initialise the announce
        val announce = _db.child(Statics.FIREBASE_ANNOUNCE).child(objectId)
        announce.child("title").setValue(title)
        announce.child("price").setValue(price)
        announce.child("ram").setValue(ram)
        announce.child("hardDiskDrive").setValue(hardDiskDrive)
        announce.child("processor").setValue(processor)
        announce.child("screenWidth").setValue(screen)
        announce.child("otherComponents").setValue(other)
        announce.child("reserved").setValue(false)
        Toast.makeText(this, "Annonce modifiée : " + title, Toast.LENGTH_SHORT).show()
    }

    private fun loadAnnounceList(dataSnapshot: DataSnapshot) {
        Log.d("MainActivity", "loadAnnounceList")

        val announces = dataSnapshot.children.iterator()

        //Check if current database contains any collection
        if (announces.hasNext()) {

            _announceList!!.clear()


            val listIndex = announces.next()
            val itemsIterator = listIndex.children.iterator()

            //check if the collection has any announce or not
            while (itemsIterator.hasNext()) {

                //get current announce
                val currentItem = itemsIterator.next()
                val announce = Announce.create()

                //get current data in a map
                val map = currentItem.getValue() as HashMap<String, Any>;

                //key will return the Firebase ID
                announce.objectId = currentItem.key
                announce.reserved = map.get("reserved") as Boolean?
                announce.title = map.get("title") as String?
                announce.price = map.get("price") as String?
                announce.ram = map.get("ram") as String?
                announce.hardDiskDrive = map.get("hardDiskDrive") as String?
                announce.processor = map.get("processor") as String?
                announce.screenWidth = map.get("screenWidth") as String?
                announce.otherComponents = map.get("otherComponents") as String?


                _announceList!!.add(announce)
            }
        } else {
            _announceList!!.clear()
        }

        //alert adapter that has changed
        _adapter.notifyDataSetChanged()

    }
    fun signIn(view: View,email: String, password: String){
        showMessage(view,"Authenticating...")

        _fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { announce ->
            if(announce.isSuccessful){
                var intent = Intent(this, LoggedInActivity::class.java)
                intent.putExtra("id", _fbAuth.currentUser?.email)
                startActivity(intent)

            }else{
                showMessage(view,"Error: ${announce.exception?.message}")
            }
        })

    }

    fun showMessage(view:View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }


}