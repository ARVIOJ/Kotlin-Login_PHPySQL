package helper

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log

class SessionManager(context: Context) {
    // LogCat tag
    private val tag = SessionManager::class.java.simpleName
    // Shared Preferences
    var pref: SharedPreferences? = null
    var editor: Editor? = null
    var _context: Context? = null
    // Shared pref mode
    var PRIVATE_MODE = 0
    // Shared preferences file name
    private val PREF_NAME = "LoginSQL"
    private val KEY_IS_LOGGEDIN = "isLoggedIn"
    init{
        _context = context
        pref = _context?.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref!!.edit()
        editor!!.apply()
    }
    fun setLogin(isLoggedIn: Boolean) {
        editor!!.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn)
        // commit changes
        editor!!.commit()
        Log.d(tag, "User login session modified!")
    }
    fun isLoggedIn(): Boolean {
        return pref!!.getBoolean(KEY_IS_LOGGEDIN, false)
    }
}
