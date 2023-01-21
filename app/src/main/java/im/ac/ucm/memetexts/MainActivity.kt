package im.ac.ucm.memetexts

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import im.ac.ucm.memetexts.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, resources.getString(R.string.meme_start_text), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        binding.mainContent.spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.spinner_options))
        binding.mainContent.spinner.onItemSelectedListener = this
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    var selected: String = "any"
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long){
        selected = parent.getItemAtPosition(pos).toString()
        //Log.wtf("selected", selected)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selected = "any"
    }

    fun buttonClick(view: View){
        var url = "https://meme-api.com/gimme/"
        if(selected != "any"){
            url += selected;
        }
        val vm = VolleyManager(this)
        vm.call(
            url
        ) { response ->
            val m = Meme(
                response.toString()
            )
            if(m.isNsfw()){
                binding.mainContent.button.callOnClick()
            }
            else{
                m.postImage(binding.mainContent.textView, binding.mainContent.button, binding.mainContent.imageView)
            }
            val twilioUrl = "https://meme-1374.twil.io/send?msgtext=Check out this meme! ${response["url"]}"

            binding.fab.setOnClickListener { view ->
                //add to db here
                TODO("move to from db")
                vm.call(
                    twilioUrl
                ) { response ->
                    //Log.wtf("success", response.toString())
                    val obj = JSONObject(response.toString())
                    Snackbar.make(view, "${obj["return"]} to ${obj["to"]}", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
        }
    }
}