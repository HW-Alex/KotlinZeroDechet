package com.example.zerodechet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_announce_detail.*

class AnnounceDetailActivity : AppCompatActivity(){

    val mailPro = "machintruc77@gmail.com"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announce_detail)
        val title = intent.getStringExtra("title")
        val price = intent.getStringExtra("price")
        val ram = intent.getStringExtra("ram")
        val hardDiskDrive = intent.getStringExtra("hardDiskDrive")
        val processor = intent.getStringExtra("processor")
        val screenWidth = intent.getStringExtra("screenWidth")
        val otherComponents = intent.getStringExtra("otherComponents")
        val url = intent.getStringExtra("url")
        txtTitle.setText(title)
        txtRam.setText(ram)
        txtHardDiskDrive.setText(hardDiskDrive)
        txtPrice.setText(price)
        txtOther.setText(otherComponents)
        txtScreenWidth.setText(screenWidth)
        txtProcessor.setText(processor)
        Picasso.get().load(url).into(imageView)

        btnReservation.setOnClickListener {

        }
        btnContact.setOnClickListener {

        }
    }

    fun contactForm(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Nous Contacter")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_announce, null)
        val txtSubject  = dialogLayout.findViewById<EditText>(R.id.txtSubject)
        val txtMessage  = dialogLayout.findViewById<EditText>(R.id.txtMessage)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Envoyer") {
                dialogInterface, i ->
            sendEmail(mailPro, txtSubject.toString(), txtMessage.toString())
        }
        builder.show()
    }

    private fun sendEmail(recipient: String, subject: String, message: String) {
        /*ACTION_SEND action to launch an email client installed on your Android device.*/
        val mIntent = Intent(Intent.ACTION_SEND)
        /*To send an email you need to specify mailto: as URI using setData() method
        and data type will be to text/plain using setType() method*/
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        // put recipient email in intent
        /* recipient is put as array because you may wanna send email to multiple emails
           so enter comma(,) separated emails, it will be stored in array*/
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        //put the Subject in the intent
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        //put the message in the intent
        mIntent.putExtra(Intent.EXTRA_TEXT, message)
        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }

    }

}