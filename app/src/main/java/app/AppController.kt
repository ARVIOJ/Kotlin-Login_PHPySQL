package app

import android.app.Application
import android.content.Context
import android.text.TextUtils
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class AppController(context: Context) : Application() {
    val TAG = AppController::class.java.simpleName
    private var mInstance: AppController? = null
    private var mRequestQueue: RequestQueue? = null
    private var context:Context? = null
    override fun onCreate() {
        super.onCreate()
        mInstance = this.getInstance()
    }
    init {
        this.context = context
    }
    @Synchronized fun getInstance(): AppController? {
        if (mInstance == null) {
            mInstance = this
        }
        return mInstance
    }
    private fun getRequestQueue(): RequestQueue?
    {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context)
        }
        return mRequestQueue
    }
    fun addToRequestQueue(req: StringRequest, tag: String) {
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        getRequestQueue()!!.add(req)
    }
    fun addToRequestQueue(req: StringRequest) {
        req.tag = TAG
        getRequestQueue()!!.add(req)
    }
    fun cancelPendingRequests(tag: Any?) {
        if (mRequestQueue != null) {
            mRequestQueue!!.cancelAll(tag)
        }
    }
}