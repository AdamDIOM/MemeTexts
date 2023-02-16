package im.ac.ucm.memetexts

import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class VolleyManager(context: Context){
    private val q = Volley.newRequestQueue(context)

    fun call(API_URL: String, listener: Response.Listener<org.json.JSONObject>, failListener: Response.ErrorListener?){
        var list = mutableListOf<Int>()

        Log.wtf("list", "complete")
        val jOR = JsonObjectRequest(
            Request.Method.GET,
            API_URL,
            null,
            listener,
            failListener
        )
        q.add(jOR)
    }

    // handles errors returned by the API
    fun fail(context: Context, response: String, tView: TextView){
        Log.wtf("fail", "fail on $response")
        if(response.contains("com.android.volley.NoConnectionError", true)){
            tView.text = context.getString(R.string.no_internet)
        }
        else{
            tView.text = context.getString(R.string.retrieval_failed)
        }
    }
    // supports fail with or without button
    fun fail(context: Context, response: String, tView: TextView, button: Button){
        fail(context, response, tView)
        button.isEnabled = true
    }
}