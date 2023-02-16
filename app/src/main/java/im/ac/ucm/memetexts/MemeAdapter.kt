package im.ac.ucm.memetexts

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import im.ac.ucm.memetexts.databinding.CardviewMemeBinding
import org.json.JSONObject

class MemeAdapter : RecyclerView.Adapter<MemeAdapter.MemeViewHolder>() {

    var memes = mutableListOf<Meme>()

    inner class MemeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding = CardviewMemeBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_meme, parent, false)
        return MemeViewHolder(view)
    }

    private fun createSnackBar(view: View, text: String){
        Snackbar.make(
            view,
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }
    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        val meme = memes[position]
        Log.wtf("meme", meme.toString())
        meme.postImageViewOnly(holder.binding.textView, holder.binding.imageView2)

        holder.binding.button.setOnClickListener { view ->
            val vm = VolleyManager(holder.binding.imageView2.context)
            val twilioUrl = "https://meme-1374.twil.io/send?msgtext=Check out this meme! ${meme.getImgUrl()}"
            vm.call(
                twilioUrl,
                { response ->
                    //Log.wtf("success", response.toString())
                    val obj = JSONObject(response.toString())
                    createSnackBar(view, "${obj["return"]} to ${obj["to"]}")
                },
                { response ->
                    createSnackBar(view, "Send to phone failed")
                }
            )
        }
    }

    override fun getItemCount(): Int {
        return memes.size
    }
}