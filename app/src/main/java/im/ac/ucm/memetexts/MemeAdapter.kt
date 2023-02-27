package im.ac.ucm.memetexts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import im.ac.ucm.memetexts.databinding.CardviewMemeBinding
import im.ac.ucm.memetexts.main.MainActivity
import im.ac.ucm.memetexts.settings.SettingsActivity
import org.json.JSONObject

class MemeAdapter(context: Context) : RecyclerView.Adapter<MemeAdapter.MemeViewHolder>() {

    var memes = mutableListOf<Meme>()

    private val context: DBViewActivity = context as DBViewActivity

    inner class MemeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding = CardviewMemeBinding.bind(itemView)
    }

    /**Inflates the view and returns a holder*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_meme, parent, false)
        return MemeViewHolder(view)
    }

    /**Method to create and display a snackbar with given text.
     * @param view the view to display the Snackbar in
     * @param text the text to show on the Snackbar*/
    private fun createSnackbar(view: View, text: String){
        Snackbar.make(
            view,
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }

    /**Sends the current meme to the phone number stored in SharedPrefs.
     * @param holder the current MemeViewHolder
     * @param view the view to show a Snackbar in
     * @param preferenceManager the variable storing the SharedPrefs link
     * @param meme the meme to send to phone*/
    private fun sendToPhoneButtonHandler(holder: MemeViewHolder, view: View, preferenceManager: SharedPreferences, meme: Meme){
        val vm = VolleyManager(holder.binding.imageView2.context)
        val twilioUrl =
            "https://meme-1374.twil.io/send?" +
                    "to=" +
                    preferenceManager.getString("phone_num", "+447624420298") +
                    "&msgtext=" +
                    preferenceManager.getString("pre_text", "Check out this meme!") + " " +
                    meme.getImgUrl() + " " +
                    preferenceManager.getString("post_text", "")
        vm.call(
            twilioUrl,
            // success handler
            { response ->
                val obj = JSONObject(response.toString())
                createSnackbar(view, "${obj["return"]} to ${obj["to"]}")
            },
            // fail handler
            { response ->
                createSnackbar(
                    view,
                    if(response.toString().contains("com.android.volley.NoConnectionError", true)){
                        context.getString(R.string.no_internet)
                    }
                    else{
                        context.getString(R.string.send_to_phone_failed)
                    }
                )
                Log.wtf("fail", response.toString())
            }
        )
    }

    /**Launches MainActivity and calls the meme API using current meme's subreddit.
     * @param meme the meme to get similar meme from*/
    private fun getSimilarMemeButtonHandler(meme: Meme){
        val sub = meme.getSubreddit()
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("sub", sub)
        context.setResult(Activity.RESULT_OK, intent)
        context.finish()
    }

    /**Deletes the current meme from the database and array.
     * @param view the view to show Snackbar from
     * @param meme the meme to delete*/
    private fun deleteButtonHandler(view: View, meme: Meme){
        val dbM = DbManager(context)
        if(dbM.delete(meme)){
            createSnackbar(view, "Deleted Meme ${meme.getPostTitle()}")
            memes.remove(meme)
            this.notifyDataSetChanged()
        }
        else{
            createSnackbar(view, "Failed to delete meme ${meme.getPostTitle()}")
        }
    }

    /**Initialisation code for each of the MemeViewHolders
     * @param holder the MemeViewHolder that is being displayed
     * @param position the current meme's position in the memes list*/
    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        val meme = memes[position]
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)

        meme.postImageViewOnly(holder.binding.textView, holder.binding.imageView2)

        if(!preferenceManager.getString("phone_num", "").isNullOrEmpty()) {
            holder.binding.sendToPhoneButton.text = context.resources.getString(R.string.send_to_phone)
            holder.binding.sendToPhoneButton.setOnClickListener { view -> sendToPhoneButtonHandler(holder, view, preferenceManager, meme) }
        }
        else{
            holder.binding.sendToPhoneButton.text = context.getString(R.string.add_phone_num)
            holder.binding.sendToPhoneButton.setOnClickListener {
                val intent = Intent(context, SettingsActivity::class.java)
                context.startActivity(intent)
            }
        }

        holder.binding.getSimilarButton.setOnClickListener { getSimilarMemeButtonHandler(meme) }

        holder.binding.deleteButton.setOnClickListener{ view -> deleteButtonHandler(view, meme) }
    }

    /**Gets number of items in the adapter.
     * @return number of items in the adapter*/
    override fun getItemCount(): Int {
        return memes.size
    }
}