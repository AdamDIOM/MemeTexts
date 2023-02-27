package im.ac.ucm.memetexts

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream
import java.util.*

class DbManager(context: Context): java.io.Serializable {
    private var helper: DbHelper = DbHelper(context)

    /**Creates a record in the database for the given meme.
     * @param meme the meme to add to database.*/
    fun create(meme: Meme){
        val contentValues = ContentValues();
        contentValues.put("Title", meme.getPostTitle())
        contentValues.put("Subreddit", meme.getSubreddit())
        contentValues.put("PostLink", meme.getPostLink())
        contentValues.put("ImgUrl", meme.getImgUrl())
        contentValues.put("ImgData", meme.getImgData())
        contentValues.put("NSFW", meme.isNsfw())
        helper.writableDatabase.insert("tblSavedMemes", null, contentValues)
    }

    /**Takes values for the given row of the cursor.
     * @param cursor the current position in the database.
     * @return the newly created meme.*/
    private fun createMemeFromValues(cursor: android.database.Cursor): Meme{
        val idColIndex = cursor.getColumnIndex("Id")
        val id = cursor.getInt(idColIndex)
        val titleColIndex = cursor.getColumnIndex("Title")
        val title = cursor.getString(titleColIndex)
        val subColIndex = cursor.getColumnIndex("Subreddit")
        val sub = cursor.getString(subColIndex)
        val postLinkColIndex = cursor.getColumnIndex("PostLink")
        val postLink = cursor.getString(postLinkColIndex)
        val imgUrlColIndex = cursor.getColumnIndex("ImgUrl")
        val imgUrl = cursor.getString(imgUrlColIndex)
        val nsfwColIndex = cursor.getColumnIndex("NSFW")
        val nsfw = cursor.getInt(nsfwColIndex) == 1
        val imgDataColIndex = cursor.getColumnIndex("ImgData")
        val imgDataBlob = cursor.getBlob(imgDataColIndex)

        return if(imgDataBlob == null){
            Meme(title, postLink, imgUrl, nsfw, sub)
        } else{
            Meme(id, title, postLink, imgUrl, nsfw, imgDataBlob, sub)
        }
    }

    /**Gets a list of all memes in the database.
     * @return a list of all memes in the database.*/
    fun retrieve() : List<Meme> {
        val memes = mutableListOf<Meme>()
        val qry = "SELECT * FROM tblSavedMemes ORDER BY Id DESC"
        val cursor = helper.readableDatabase.rawQuery(qry, null)
        // if there is data, move to the start and loop through all lines.
        if(cursor.moveToFirst()){
            do{
                val meme = createMemeFromValues(cursor)
                memes.add(meme)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return memes
    }

    /**Deletes the given meme.
     * @param meme the meme to delete.
     * @return True if the meme successfully deletes, false if it fails.*/
    fun delete(meme: Meme?) : Boolean{
        if(meme?.getPostLink() != null){
            return try {
                helper.writableDatabase.delete(
                    "tblSavedMemes",
                    "ID=?",
                    arrayOf(meme.getID()!!.toString())
                )
                true
            } catch(e: Exception){
                false
            }
        }
        return false
    }

    /**Ends the database session*/
    fun close(){
        helper.close()
    }
}