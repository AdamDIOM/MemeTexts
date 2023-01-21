package im.ac.ucm.memetexts

import android.content.ContentValues
import android.content.Context
import java.util.*

class DbManager(context: Context) {
    private var helper: DbHelper = DbHelper(context)

    fun create(meme: Meme){
        val contentValues = ContentValues();
        contentValues.put("Title", meme.getPostTitle())
        contentValues.put("PostLink", meme.getPostLink())
        contentValues.put("ImgUrl", meme.getImgUrl())
        helper.writableDatabase.insert("tblSavedMemes", null, contentValues)
    }

    fun retrieve() : List<Meme> {
        val memes = mutableListOf<Meme>()
        val qry = "SELECT * FROM tblSavedMemes ORDER BY Id DESC"
        val cursor = helper.readableDatabase.rawQuery(qry, null)
        if(cursor.moveToFirst()){
            do{
                val idColIndex = cursor.getColumnIndex("Id")
                val id = cursor.getInt(idColIndex)
                val titleColIndex = cursor.getColumnIndex("Title")
                val title = cursor.getString(titleColIndex)
                val postLinkColIndex = cursor.getColumnIndex("PostLink")
                val postLink = cursor.getString(postLinkColIndex)
                val imgUrlColIndex = cursor.getColumnIndex("ImgUrl")
                val imgUrl = cursor.getString(imgUrlColIndex)

                val meme = Meme(title, postLink, imgUrl, false)

                memes.add(meme)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return memes
    }

    // should never need updating
    fun update(meme: Meme){

    }

    fun delete(meme: Meme){
        if(meme.getPostLink() != null){
            helper.writableDatabase.delete("tblSavedMemes", "PostLink=?", arrayOf(meme.getPostLink()))
        }
    }

    fun close(){
        helper.close()
    }
}