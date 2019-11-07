package com.example.zerodechet

    import android.os.Bundle
    import android.view.View
    import android.widget.Button
    import androidx.appcompat.app.AppCompatActivity
    import com.google.android.material.snackbar.Snackbar
    import com.google.firebase.auth.FirebaseAuth


    class LoggedInActivity : AppCompatActivity() {
        var fbAuth = FirebaseAuth.getInstance()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_logged_in)

            var btnLogOut = findViewById<Button>(R.id.btnLogout)

            btnLogOut.setOnClickListener{ view ->
                showMessage(view, "Logging Out...")
                signOut()
            }

            fbAuth.addAuthStateListener {
                if(fbAuth.currentUser == null){
                    this.finish()
                }
            }
        }

        fun signOut(){
            fbAuth.signOut()

        }

        fun showMessage(view: View, message: String){
            Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
        }

    }
