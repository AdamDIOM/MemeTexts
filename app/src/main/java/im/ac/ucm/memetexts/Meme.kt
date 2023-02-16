package im.ac.ucm.memetexts

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.IOException
import java.net.URL

/** @param t Meme title
 * @param pL Reddit post link
 * @param u Image url
 * @param n Whether the meme is safe for work or not
 * */
class Meme(t: String, pL: String, u: String, n: Boolean): AppCompatActivity(), java.io.Serializable{

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
    /** @param jsonData A JSON object containing the title, postLink, url and whether the meme is not safe for work */
    constructor(jsonData: String) : this(
        JSONObject(jsonData)["title"].toString(),
        JSONObject(jsonData)["postLink"].toString(),
        JSONObject(jsonData)["url"].toString(),
        JSONObject(jsonData)["nsfw"].toString() == "true"
    )

    /** @return Whether the meme is not safe for work  */
    fun isNsfw(): Boolean {
        return nsfw
    }
    /** @return the title of the meme */
    fun getPostTitle(): String {
        return title
    }
    /** @return the link to the Reddit post that the meme is from */
    fun getPostLink(): String {
        return postLink
    }
    /** @return the direct url to the image */
    fun getImgUrl(): String {
        return imgUrl
    }

    /** Gets the image from the url and sets it. Disables given button
     * @param tView a TextView to display the meme title
     * @param iView an ImageView to display the meme
     * @param buttonParam meme generator button to enable after success/fail of meme output */
    fun postImage(tView: android.widget.TextView, iView: android.widget.ImageView, buttonParam: android.widget.Button) {

        val thread = Thread {
            try {
                val bitmap = BitmapFactory.decodeStream(URL(imgUrl).openStream())
                runOnUiThread {
                    tView.text = title
                    iView.setImageBitmap(bitmap)
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

    /** Gets image from the url and sets it, does not disable any button
     * @param tView a TextView to display the meme title
     * @param iView an ImageView to display the meme */
    fun postImageViewOnly(tView: android.widget.TextView, iView: android.widget.ImageView){
        val thread = Thread {
            try {
                val bitmap = BitmapFactory.decodeStream(URL(imgUrl).openStream())
                runOnUiThread {
                    tView.text = title
                    iView.setImageBitmap(bitmap)
                }
            } catch (e: IOException) {
                runOnUiThread {
                    tView.text = getString(R.string.img_display_failed)
                }
            }
        }
        thread.start()
    }
}