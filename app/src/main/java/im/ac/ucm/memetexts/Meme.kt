package im.ac.ucm.memetexts

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.URL

/** @param t Meme title
 * @param pL Reddit post link
 * @param u Image url
 * @param n Whether the meme is safe for work or not
 * @param s Subreddit that the meme is from
 * */
class Meme(t: String, pL: String, u: String, n: Boolean, s: String): AppCompatActivity(), java.io.Serializable{

    private val title: String
    private val postLink: String
    private lateinit var imgData: ByteArray
    private val imgUrl: String
    private val nsfw: Boolean
    private val sub: String
    private var id: Int?

    init {
        title = t
        postLink = pL
        imgUrl = u
        nsfw = n
        sub = s
        id = null
    }
    /** @param i Database ID
     * @param t Meme title
     * @param pL Reddit post link
     * @param u Image url
     * @param n Whether the meme is safe for work or not
     * @param d Image data as bitmap
     * @param s Subreddit that the meme is from
     * */
    constructor(i: Int, t: String, pL: String, u: String, n: Boolean, d: ByteArray, s: String) : this(
        t,
        pL,
        u,
        n,
        s
    ){
        imgData = d
        id = i
    }

    /** @param jsonData A JSON object containing the title, postLink, url and whether the meme is not safe for work */
    constructor(jsonData: String) : this(
        JSONObject(jsonData)["title"].toString(),
        JSONObject(jsonData)["postLink"].toString(),
        JSONObject(jsonData)["url"].toString(),
        JSONObject(jsonData)["nsfw"].toString() == "true",
        JSONObject(jsonData)["subreddit"].toString()
    )

    /** @return Whether the meme is not safe for work  */
    fun isNsfw(): Boolean {
        return nsfw
    }
    /** @return the title of the meme */
    fun getPostTitle(): String {
        return title
    }
    /** @return the subreddit the meme is from */
    fun getSubreddit(): String {
        return sub
    }
    /** @return the link to the Reddit post that the meme is from */
    fun getPostLink(): String {
        return postLink
    }
    /** @return the direct url to the image */
    fun getImgUrl(): String {
        return imgUrl
    }
    /** @return the image string */
    fun getImgData(): ByteArray {
        return imgData
    }
    /** @return the ID of the meme in the database if it exists, otherwise returns null */
    fun getID(): Int? {
        return id
    }

    /**Downloads the image from the url converts it for display.*/
    private fun initImg(){
        val bitmap = BitmapFactory.decodeStream(URL(imgUrl).openStream())
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        imgData = stream.toByteArray()
    }

    /**Takes the existing image and compresses it to 80% width nad height.*/
    private fun compressImg(){
        val bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.size)
        val stream = ByteArrayOutputStream()
        val resized = Bitmap.createScaledBitmap(bitmap, (bitmap.width*0.8).toInt(), (bitmap.height*0.8).toInt(), true)
        resized.compress(Bitmap.CompressFormat.PNG, 100, stream)
        imgData = stream.toByteArray()
    }

    /**Posts the image to the given ImageView and updates the title.
     * @param tView a TextView to display the meme title
     * @param iView an ImageView to display the meme*/
    private fun postImage(tView: TextView, iView: ImageView){
        try {
            if(!this::imgData.isInitialized){
                initImg()
            }
            while(imgData.size > 500000){
                compressImg()
            }
            val bmImg = BitmapFactory.decodeByteArray(imgData, 0, imgData.size)
            runOnUiThread {
                tView.text =
                    if(nsfw) {"⚠️$title⚠️"}
                    else {title}
                iView.setImageBitmap(bmImg)
            }
        } catch (e: Exception) {
            runOnUiThread {
                tView.text = getString(R.string.img_display_failed)
            }
        }
    }

    /** Gets the image from the url and sets it. Disables given button.
     * @param tView a TextView to display the meme title
     * @param iView an ImageView to display the meme
     * @param buttonParam meme generator button to enable after success/fail of meme output */
    fun postImageButton(tView: TextView, iView: ImageView, buttonParam: android.widget.Button) {

        val thread = Thread {
            postImage(tView, iView)
            runOnUiThread {
                buttonParam.isEnabled = true
            }
        }
        thread.start()
    }

    /** Gets image from the url and sets it, does not disable any button.
     * @param tView a TextView to display the meme title
     * @param iView an ImageView to display the meme */
    fun postImageViewOnly(tView: TextView, iView: ImageView){
        val thread = Thread {
            postImage(tView, iView)
        }
        thread.start()
    }
}