package com.example.zerodechet

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    lateinit var _db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        _db = FirebaseDatabase.getInstance().reference
        fab.setOnClickListener { view ->
           showFooter()
        }
        btnAdd.setOnClickListener{addTask()}
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
    fun showFooter(){
        footer.visibility = View.VISIBLE
        fab.visibility = View.GONE
    }
    private fun addTask(){

        //Declare and Initialise the Task
        val task = Task.create()

        //Set Task Description and isDone Status
        task.taskDesc = txtNewTaskDesc.text.toString()
        task.done = false

        //Get the object id for the new task from the Firebase Database
        val newTask = _db.child(Statics.FIREBASE_TASK).push()
        task.objectId = newTask.key

        //Set the values for new task in the firebase using the footer form
        newTask.setValue(task)

        //Hide the footer and show the floating button
        footer.visibility = View.GONE
        fab.visibility = View.VISIBLE

        //Reset the new task description field for reuse.
        txtNewTaskDesc.setText("")

        Toast.makeText(this, "Task added to the list successfully" + task.objectId, Toast.LENGTH_SHORT).show()
    }
}
