package activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import app.AppConfig
import app.AppController
import com.android.volley.toolbox.StringRequest
import com.arvioj.loginphpysql.MainActivity
import com.arvioj.loginphpysql.R
//import com.example.loginphpysql.R
import helper.SQLiteHandler
import helper.SessionManager
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
class Login : AppCompatActivity(), CoroutineScope{
    private val tag = Login::class.java.simpleName
    private var btnLogin: Button? = null
    private var btnLinkToRegister: Button? = null
    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var session: SessionManager? = null
    private var db: SQLiteHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        inputEmail = findViewById(R.id.email)
        inputPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.btnLogin)
        btnLinkToRegister = findViewById(R.id.btnLinkToRegisterScreen)
// SQLite database handler
        db = SQLiteHandler(applicationContext)
// Session manager
        session = SessionManager(this)
// Check if user is already logged in or not
        if (session!!.isLoggedIn()) {
// User is already logged in. Take him to main activity
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
// Login button Click Event
        btnLogin!!.setOnClickListener {
            val email = inputEmail!!.text.toString()
            val password = inputPassword!!.text.toString()
// Check for empty data in the form
            if (email.isNotEmpty() && password.isNotEmpty()) {
                it.hideKeyboard()
// login user
                checkLogin(email, password)
            } else {
// Prompt user to enter credentials
                Toast.makeText(
                    applicationContext,

                    "Please enter the credentials!", Toast.LENGTH_LONG

                ).show()
            }
        }
// Link to Register Screen
        btnLinkToRegister!!.setOnClickListener{
            val i = Intent(
                applicationContext,
                Register::class.java
            )
            startActivity(i)
            finish()
        }
    }
    /**
     * function to verify login details in mysql db
     */
    private fun checkLogin(email: String, password: String) {
// Tag used to cancel the request
        val tagStringReq = "req_login"
        showDialog()
        val strReq = object : StringRequest(Method.POST,
            AppConfig().getURL("login"), { response ->
                Log.d(tag, "Login Response: $response")
                hideDialog()
                try {
                    val jObj =

                        JSONObject(response)
                    val error = jObj.getBoolean("error")
                    Log.d(tag, "Error Response: $error")
// Check for error node in json

                    if (!error) {

// user successfully logged in
// Create login session
                        session!!.setLogin(true)
// Now store the user in SQLite
                        val uid = jObj.getString("uid")
                        val user = jObj.getJSONObject("user")
                        val username = user.getString("name")
                        val useremail = user.getString("email")
                        val usercreatedAt =
                            user.getString("created_at")
                        val userupdatedAt =
                            user.getString("updated_at")
// Inserting row in users table
                        db!!.addUser(username, useremail, uid,

                            usercreatedAt, userupdatedAt)
// Launch main activity
                        val intent = Intent(
                            this@Login,

                            MainActivity::class.java

                        )

                        startActivity(intent)
                        finish()
                    } else {
// Error in login. Get the error message
                        val errorMsg = jObj.getString("error_msg") as

                                String
                        Toast.makeText(applicationContext,

                            errorMsg, Toast.LENGTH_LONG

                        ).show()
                    }
                } catch (e: JSONException) {
// JSON error
                    e.printStackTrace()
                    Toast.makeText(
                        applicationContext,
                        "Json error: " + e.message,

                        Toast.LENGTH_LONG
                    ).show()
                }
            }, { error ->
                Log.e(tag, "Login Error: $error")
                Toast.makeText(
                    applicationContext,

                    error.message, Toast.LENGTH_LONG

                ).show()
                hideDialog()
            }){
            override fun getParams(): Map<String, String> {
// Posting parameters to login url
                val params: MutableMap<String, String> = HashMap()
                params["email"] = email
                params["password"] = password
                return params
            }
        }
        AppController::getInstance.call(AppController(applicationContext))?.addToRequestQueue(
            strReq,
            tagStringReq
        )
    }
    private fun showDialog() {
        val parent = findViewById<ViewGroup>(R.id.frameLoginLayout)
        val asyncLayoutInflater = AsyncLayoutInflater(this)
        launch {
            val view =
                asyncLayoutInflater.inflate(R.layout.progress_bar,
                    parent)
            parent.addView(view)
        }
    }
    private fun hideDialog() {
        val parent = findViewById<ViewGroup>(R.id.frameLoginLayout)
        val asyncLayoutInflater = AsyncLayoutInflater(this)
        launch {
            val view = asyncLayoutInflater.inflate(R.layout.login,
                parent)
            parent.addView(view)
        }
    }
    private val activityJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + activityJob
    private suspend fun AsyncLayoutInflater.inflate(@LayoutRes resid:
                                                    Int,
                                                    parent: ViewGroup):
            View =
        suspendCoroutine { continuation -> inflate(resid, parent) {
                view, _, _
            -> continuation.resume(view) } }
    private fun View.hideKeyboard() {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as
                    InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    override fun onDestroy() {
        super.onDestroy()
        activityJob.cancel()
    }
}