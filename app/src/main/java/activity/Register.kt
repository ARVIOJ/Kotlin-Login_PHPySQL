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
import helper.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Register: AppCompatActivity(), CoroutineScope {
    private val tag = Register::class.java.simpleName
    private var btnRegister: Button? = null
    private var btnLinkToLogin: Button? = null
    private var inputFullName: EditText? = null
    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var session: SessionManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        inputFullName = findViewById<View>(R.id.name) as EditText
        inputEmail = findViewById<View>(R.id.email) as EditText
        inputPassword = findViewById<View>(R.id.password) as EditText
        btnRegister = findViewById<View>(R.id.btnRegister) as Button
        btnLinkToLogin = findViewById<View>(R.id.btnLinkToLoginScreen)
                as Button
        // Session manager
        session = SessionManager(this)
        // Check if user is already logged in or not
        if (session!!.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            val intent = Intent(
                this@Register,
                MainActivity::class.java
            )
            startActivity(intent)
            finish()
        }
        btnRegister!!.setOnClickListener {
            val name = inputFullName!!.text.toString()
            val email = inputEmail!!.text.toString()
            val password = inputPassword!!.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty() &&
                password.isNotEmpty()) {
                it.hideKeyboard()
                registerUser(name, email, password)
            } else {
                Toast.makeText(
                    applicationContext,
                    "Please enter your details!", Toast.LENGTH_LONG
                )
                    .show()
            }
        }
        btnLinkToLogin!!.setOnClickListener {
            val i = Intent(
                applicationContext,
                Login::class.java
            )
            startActivity(i)
            finish()
        }
    }
    /**
     * Function to store user in MySQL database will post params(tag,
    name,
     * email, password) to register url
     */
    private fun registerUser(
        name: String, email: String,
        password: String
    ) {
        // Tag used to cancel the request
        val tagStringReq = "req_register"
        showDialog()
        val strReq = object :
            StringRequest(Method.POST,AppConfig().getURL("register"), { response ->
                Log.d(tag, "Register Response: $response")
                hideDialog()
                try {
                    val jObj = JSONObject(response)
                    val error = jObj.getBoolean("error")
                    Log.d(tag, "Error Response: $error")
                    // Check for error node in json
                    if (!error) {
                        // user created successfully
                        Toast.makeText(applicationContext,
                            getString(R.string.user_registed), Toast.LENGTH_LONG).show()
                    } else {
                        // Error in register. Get the error message
                        val errorMsg = jObj.getString("error_msg") as
                                String
                        Toast.makeText(
                            applicationContext,
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
            }, {error ->
                Log.e(tag, "Register Error: $error")
                Toast.makeText(
                    applicationContext,
                    error.message, Toast.LENGTH_LONG
                ).show()
                hideDialog()
            }){
            override fun getParams(): Map<String, String> {
// Posting parameters to Register url
                val params: MutableMap<String, String> = HashMap()
                params["name"] = name
                params["email"] = email
                params["password"] = password
                return params
            }
        }

        AppController::getInstance.call(AppController(applicationContext))?.addToRequestQueue(strReq, tagStringReq)
    }
    private fun showDialog() {
        val parent = findViewById<ViewGroup>(R.id.frameRegisterLayout)
        val asyncLayoutInflater = AsyncLayoutInflater(applicationContext)
        launch {
            val view =
                asyncLayoutInflater.inflate(R.layout.progress_bar, parent)
            parent.addView(view)
        }
    }
    private fun hideDialog() {
        val parent = findViewById<ViewGroup>(R.id.frameRegisterLayout)
        val asyncLayoutInflater =
            AsyncLayoutInflater(applicationContext)
        launch {
            val view = asyncLayoutInflater.inflate(R.layout.register,
                parent)
            parent.addView(view)
        }
    }
    private val activityJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + activityJob
    private suspend fun AsyncLayoutInflater.inflate(@LayoutRes resid: Int, parent: ViewGroup): View =
        suspendCoroutine { continuation -> inflate(resid, parent) {
                view, _, _ -> continuation.resume(view) } }
    private fun View.hideKeyboard() {
        val imm =
            context.getSystemService(INPUT_METHOD_SERVICE) as
                    InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    override fun onDestroy() {
        super.onDestroy()
        activityJob.cancel()
    }
}
