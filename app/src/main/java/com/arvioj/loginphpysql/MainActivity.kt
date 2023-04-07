package com.arvioj.loginphpysql

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import activity.Login
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import helper.SessionManager
import helper.SQLiteHandler

class MainActivity : AppCompatActivity() {
    private var txtUid: TextView? = null
    private var txtName: TextView? = null
    private var txtEmail: TextView? = null
    private var txtCreated: TextView? = null
    private var txtUpdated: TextView? = null
    private var btnLogout: Button? = null
    private var db: SQLiteHandler? = null
    private var session: SessionManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtUid = findViewById<View>(R.id.uid) as TextView
        txtName = findViewById<View>(R.id.name) as TextView
        txtEmail = findViewById<View>(R.id.email) as TextView
        txtCreated = findViewById<View>(R.id.created) as TextView
        txtUpdated = findViewById<View>(R.id.updated) as TextView
        btnLogout = findViewById<View>(R.id.btnLogout) as Button
        // SqLite database handler
        db = SQLiteHandler(applicationContext)
        // session manager
        session = SessionManager(this)
        if (!session!!.isLoggedIn()) {
            logoutUser()
        }
        // Fetching user details from sqlite
        val user = db!!.getUserDetails()
        val uid = user!!["unique_id"]
        val name = user["name"]
        val email = user["email"]
        val created = user["created_at"]
        val updated = user["updated_at"]
        // Displaying the user details on the screen
        txtUid!!.text = uid
        txtName!!.text = name
        txtEmail!!.text = email
        txtCreated!!.text = created
        txtUpdated!!.text = updated
        btnLogout!!.setOnClickListener {
            logoutUser()
        }
    }
    /**
     * Logging out the user. Will set isLoggedIn flag to false in
    shared
     * preferences Clears the user data from sqlite users table
     */
    private fun logoutUser() {
        this.session!!.setLogin(false)
        db!!.deleteUsers()
        // Launching the login activity
        val intent = Intent(this@MainActivity, Login::class.java)
        startActivity(intent)
        finish()
    }
}