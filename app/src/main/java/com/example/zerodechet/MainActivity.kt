package com.example.zerodechet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar


class MainActivity : AppCompatActivity() {

    var _fbAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        btnLogin.setOnClickListener {view ->
            signIn(view,"alex@ynov.com", "mdpmdp")
        }
        btnAnnounce.setOnClickListener {view ->
            val intent = Intent(view.context, AnnounceActivity::class.java)
            // start your next activity
            ContextCompat.startActivity(view.context, intent, null)
        }
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