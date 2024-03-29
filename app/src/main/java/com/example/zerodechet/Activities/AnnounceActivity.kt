package com.example.zerodechet.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.zerodechet.Model.Announce
import com.example.zerodechet.Model.Statics
import com.example.zerodechet.R
import com.example.zerodechet.Services.AnnounceRowListener
import com.example.zerodechet.adapter.AnnounceAdapter
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_announce.*
import kotlinx.android.synthetic.main.content_announce.*
import java.lang.Thread.sleep


class AnnounceActivity : AppCompatActivity(), AnnounceRowListener {

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    var _urlPicture: String = ""
    lateinit var _db: DatabaseReference
    lateinit var _adapter: AnnounceAdapter
    lateinit var _imageAnnounce: ImageView
    var _announceList: MutableList<Announce>? = null
    var _announceListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            loadAnnounceList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }
    val storage = FirebaseStorage.getInstance()

    fun uploadDataToFirebase(uri: Uri) {
        val fileRef = storage.reference.child("Images_Pc").child(uri.lastPathSegment.toString())
        var uploadTask = fileRef.putFile(image_uri!!)
        val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                _urlPicture = downloadUri.toString()

            } else {
                Toast.makeText(this, "An error occur", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announce)
        _db = FirebaseDatabase.getInstance().reference
        _announceList = mutableListOf()
        _adapter = AnnounceAdapter(this, _announceList!!)
        val listAnnounces = findViewById<ListView>(R.id.listviewAnnounce)
        listAnnounces.adapter = _adapter
        listviewAnnounce!!.setAdapter(_adapter)
        btnNewAnnounce.setOnClickListener { view ->
            newAnnounceForm(view)
        }
        _db.orderByKey().addValueEventListener(_announceListener)
    }

    override fun onAnnounceChange(objectId: String, isReserved: Boolean) {
        val announce = _db.child(Statics.FIREBASE_ANNOUNCE).child(objectId).child("reserved")
        announce.setValue(isReserved)
    }
    override fun onAnnounceDelete(objectId: String) {
        val announce = _db.child(Statics.FIREBASE_ANNOUNCE).child(objectId)
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Suppression")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_deletion, null)
        dialogLayout.findViewById<TextView>(R.id.textView).setText("Souhaitez-vous vraiment supprimer cette annonce ?")
        builder.setView(dialogLayout)
        builder.setNegativeButton("Supprimer") {
                dialogInterface, i ->
            announce.removeValue()
        }
        builder.setNeutralButton("Annuler") {
                dialogInterface, i ->
        }
        builder.show()
    }
    override fun onAnnounceModify(view: View, objectId: String, title: String, price: String?,
                                  ram: String?, hardDiskDrive: String?, processor: String?,
                                  screenWidth: String?, otherComponents: String?, url: String? ) {
        modifyAnnounceForm(view, objectId, title, price, ram, hardDiskDrive,
            processor, screenWidth, otherComponents, url)
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera()
                }
                else{
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK){
            //set image captured to image view
            _imageAnnounce.setImageURI(image_uri)

            uploadDataToFirebase(image_uri!!)
            sleep(3000)
        }
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
        _imageAnnounce = dialogLayout.findViewById(R.id.imageView)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Ajouter") {
                dialogInterface, i -> addAnnounce(txtTitle.text.toString(),txtPrice.text.toString(),
            txtRam.text.toString(),txtHardDiskDrive.text.toString(),txtProcessor.text.toString(),
            txtScreenWidth.text.toString(),txtOther.text.toString())
        }
        dialogLayout.findViewById<Button>(R.id.btnImage).setOnClickListener { view ->
            cameraCheck()
        }
        builder.show()
    }
    fun cameraCheck() {
        //if system os is Marshmallow or Above, we need to request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED
            ) {
                //permission was not enabled
                val permission = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                //permission already granted
                openCamera()
            }
        } else {
            //system os is < marshmallow
            openCamera()
        }
    }
    fun modifyAnnounceForm(view: View, objectId: String, title: String, price: String?, ram: String?, hardDiskDrive: String?, processor: String?, screenWidth: String?, otherComponents: String?, url: String?) {
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
        //On récupère l'URL uniquement si il y en a une valorisée en base
        if(!url.isNullOrEmpty()) {
            Picasso.get().load(url).into(dialogLayout.findViewById<ImageView>(R.id.imageView))
        }
        val txtTitle  = dialogLayout.findViewById<EditText>(R.id.txtTitle).text
        val txtPrice  = dialogLayout.findViewById<EditText>(R.id.txtPrice).text
        val txtRam  = dialogLayout.findViewById<EditText>(R.id.txtRam).text
        val txtHardDiskDrive  = dialogLayout.findViewById<EditText>(R.id.txtHardDiskDrive).text
        val txtProcessor  = dialogLayout.findViewById<EditText>(R.id.txtProcessor).text
        val txtScreenWidth  = dialogLayout.findViewById<EditText>(R.id.txtScreenWidth).text
        val txtOther  = dialogLayout.findViewById<EditText>(R.id.txtOther).text
        _imageAnnounce = dialogLayout.findViewById(R.id.imageView)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Modifier") { dialogInterface, i ->
            modifyAnnounce(
                objectId, txtTitle.toString(), txtPrice.toString(),
                txtRam.toString(), txtHardDiskDrive.toString(), txtProcessor.toString(),
                txtScreenWidth.toString(), txtOther.toString()
            )
        }
        dialogLayout.findViewById<Button>(R.id.btnImage).setOnClickListener { view ->
            cameraCheck()
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
        announce.url = _urlPicture
        _urlPicture = ""

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
        announce.child("url").setValue(_urlPicture)
        _urlPicture = ""
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
                announce.url = map.get("url") as String?
                _announceList!!.add(announce)
            }
        } else {
            _announceList!!.clear()
        }

        //alert adapter that has changed
        _adapter.notifyDataSetChanged()
    }
}