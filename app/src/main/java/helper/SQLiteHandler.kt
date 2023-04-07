package helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SQLiteHandler(
    context: Context?,
    DATABASE_NAME: String = "id19696727_arviojdatabase",//replace your database name
    DATABASE_VERSION: Int = 1
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val TAG = SQLiteHandler::class.java.simpleName
    // Login table name
    private val TABLE_USER = "users"
    // Login Table Columns names
    private val KEY_ID = "id"
    private val KEY_NAME = "name"
    private val KEY_EMAIL = "email"
    private val KEY_UID = "unique_id"
    private val KEY_CREATED_AT = "created_at"
    private val KEY_UPDATED_AT = "updated_at"
    override fun onCreate(db: SQLiteDatabase?) {val CREATE_LOGIN_TABLE = ("CREATE TABLE " + TABLE_USER + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
        + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
        + KEY_CREATED_AT + " TEXT,"
        + KEY_UPDATED_AT + " TEXT" + ")")
        db!!.execSQL(CREATE_LOGIN_TABLE)
        Log.d(TAG, "Database tables created")
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int,
                           newVersion: Int) {
        // Drop older table if existed
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_USER)
        // Create tables again
        onCreate(db)
    }
    /**
     * Storing user details in database
     */
    fun addUser(name: String?, email: String?, uid: String?,
                created_at: String?, updated_at: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, name) // Name
        values.put(KEY_EMAIL, email) // Email
        values.put(KEY_UID, uid) // Email
        values.put(KEY_CREATED_AT, created_at) // Created At
        values.put(KEY_UPDATED_AT, updated_at) // Updated At
        // Inserting Row
        val id = db.insert(TABLE_USER, null, values)
        db.close() // Closing database connection
        Log.d(TAG, "New user inserted into sqlite: $id")
    }
    /**
     * Getting user data from database
     */
    fun getUserDetails(): HashMap<String, String>? {
        val user: HashMap<String, String> = HashMap()
        val selectQuery = "SELECT * FROM $TABLE_USER"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        // Move to first row
        cursor.moveToFirst()
        if (cursor.count > 0) {
            user["name"] = cursor.getString(1)
            user["email"] = cursor.getString(2)
            user["unique_id"] = cursor.getString(3)
            user["created_at"] = cursor.getString(4)
            user["updated_at"] = cursor.getString(5)
        }
        cursor.close()
        db.close()
        // return user
        Log.d(TAG, "Fetching user from Sqlite: $user")
        return user
    }
    /**
     * Re crate database Delete all tables and create them again
     */
    fun deleteUsers() {
        val db = this.writableDatabase
        // Delete All Rows
        db.delete(TABLE_USER, null, null)
        db.close()
        Log.d(TAG, "Deleted all user info from sqlite")
    }
}