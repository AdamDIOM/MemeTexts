package im.ac.ucm.memetexts

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class Meme(t: String, pL: String, u: String, n: Boolean): AppCompatActivity(){

    private val title: String
    private val postLink: String
    private val imgUrl: String
    private val nsfw: Boolean

    init {
        title = t
        postLink = pL
        imgUrl = u
        nsfw = n


    }

    constructor(jsonData: String) : this(
        JSONObject(jsonData)["title"].toString(),
        JSONObject(jsonData)["postLink"].toString(),
        JSONObject(jsonData)["url"].toString(),
        JSONObject(jsonData)["nsfw"].toString() == "true"
    )

    fun isNsfw(): Boolean {
        return nsfw
    }

    fun getPostTitle(): String {
        return title
    }
    fun getPostLink(): String {
        return postLink
    }
    fun getImgUrl(): String {
        return imgUrl
    }

    fun postImage(tView: android.widget.TextView, buttonParam: android.widget.Button, img: android.widget.ImageView) {

        val thread = Thread {
            try {
                val bitmap = BitmapFactory.decodeStream(URL(imgUrl).openStream())
                runOnUiThread {
                    tView.text = title
                    img.setImageBitmap(bitmap)
                    buttonParam.isEnabled = true
                }
            } catch (e: IOException) {
                runOnUiThread {
                    tView.text = getString(R.string.img_display_failed)
                    buttonParam.isEnabled = true
                }
            }
        }
        thread.start()
    }
}