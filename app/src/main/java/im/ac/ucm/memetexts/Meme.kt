package im.ac.ucm.memetexts

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class Meme(jsonData: String, tView: android.widget.TextView, buttonParam: android.widget.Button, img: android.widget.ImageView ): AppCompatActivity(){

    private val title: String
    private val postLink: String
    private val imgUrl: String
    private val nsfw: Boolean
    private val button: android.widget.Button
    private val imgView: android.widget.ImageView
    private val textView: android.widget.TextView

    init {
        val jsonObject = JSONObject(jsonData)
        title = jsonObject["title"].toString()
        postLink = jsonObject["postLink"].toString()
        imgUrl = jsonObject["url"].toString()
        nsfw = jsonObject["nsfw"].toString() == "true"
        button = buttonParam
        imgView = img
        textView = tView

        if(isNsfw()){
            button.callOnClick()
        }
        else{
            postImage()
        }
    }

    fun isNsfw(): Boolean {
        return nsfw
    }

    private fun postImage() {
        val thread = Thread {
            try {
                val bitmap = BitmapFactory.decodeStream(URL(imgUrl).openStream())
                runOnUiThread {
                    textView.text = title
                    imgView.setImageBitmap(bitmap)
                    button.isEnabled = true
                }
            } catch (e: IOException) {
                runOnUiThread {
                    textView.text = getString(R.string.img_display_failed)
                    button.isEnabled = true
                }
            }
        }
        thread.start()
    }
}