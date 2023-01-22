package im.ac.ucm.memetexts

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class VolleyManager(context: Context){
    private val q = Volley.newRequestQueue(context)

    fun call(API_URL: String, listener: Response.Listener<org.json.JSONObject>, failListener: Response.ErrorListener?){
        val jOR = JsonObjectRequest(
            Request.Method.GET,
            API_URL,
            null,
            listener,
            failListener
        )
        q.add(jOR)
    }
}