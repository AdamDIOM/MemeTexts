package im.ac.ucm.memetexts

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import im.ac.ucm.memetexts.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val options = arrayOf("any", "memes", "me_irl", "dankmemes")

        binding.mainContent.spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
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
        TODO("Not yet implemented")
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
            Meme(
                response.toString(),
                binding.mainContent.textView,
                binding.mainContent.button,
                binding.mainContent.imageView
            )
            val twilioUrl = "https://meme-1374.twil.io/send?msgtext=Check out this meme! " + response["url"].toString()
            vm.call(
                twilioUrl
            ) { response ->
                Log.wtf("success", "sent")
                //Log.wtf("success", response.toString())
                // snackbar
                Snackbar.make(view, JSONObject(response.toString())["return"].toString() + " to " + JSONObject(response.toString())["to"].toString(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            }
        }

    }
}