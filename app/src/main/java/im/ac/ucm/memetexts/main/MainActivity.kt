package im.ac.ucm.memetexts.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import im.ac.ucm.memetexts.*
import im.ac.ucm.memetexts.databinding.ActivityMainBinding
import java.io.Serializable

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbManager: DbManager

    val viewDB = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        if(result.resultCode == Activity.RESULT_OK){

        }
    }

    // function sets up all the functional components for the main activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // sets up binding for the main activity
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // sets up database connection.
        dbManager = DbManager(this)

        // sets up the Action Bar (hamburger menu)
        setSupportActionBar(binding.toolbar)

        // sets up FAB (floating action button) without a meme to favourite
        binding.fab.setOnClickListener { view ->
            Snackbar.make(
                view,
                resources.getString(R.string.meme_start_text),
                Snackbar.LENGTH_LONG
            ).show()
        }

        // sets up meme type selection Spinner
        binding.mainContent.spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(
            R.array.spinner_options
        ))
        binding.mainContent.spinner.onItemSelectedListener = this
    }

    //
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // switch over menu item selected to navigate to different Activities
        return when (item.itemId) {
            R.id.action_view_db -> {
                val intent = Intent(this, DBViewActivity::class.java)
                intent.putExtra("db", dbManager.retrieve() as Serializable)
                startActivity(intent)
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    lateinit var selected: String
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long){
        selected = parent.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // sets selected to the first item in spinner options string array ('any' or translated)
        selected = resources.getStringArray(R.array.spinner_options)[0]
    }

    private fun getUrl(): String{
        var url = "https://meme-api.com/gimme/"
        // if selected is the first item in spinner options string array ('any' or translated)
        if(selected != resources.getStringArray(R.array.spinner_options)[0]){
            url += selected;
        }
        return url
    }

    // creates instance of Meme class from API string response, checks if it is safe for work and posts to the page (or generates a new one)
    private fun makeMeme(response: String): Meme {
        val m = Meme(response)
        if(m.isNsfw()){
            binding.mainContent.button.callOnClick()
        }
        else{
            m.postImage(binding.mainContent.textView, binding.mainContent.imageView, binding.mainContent.button)
        }
        return m
    }

    // sets the FAB's onClick to add the current meme to the database and show a Snackbar output
    private fun setFAB(m: Meme){
        binding.fab.setOnClickListener{ view ->
            dbManager.create(m)
            Snackbar.make(
                view,
                "Added ${m.getPostTitle()} to Database!",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    // handles the get Meme button click
    fun buttonClick(view: View){
        binding.mainContent.button.isEnabled = false
        // gets Meme API link based on Spinner
        var url = getUrl()
        // handles the call to the Meme API
        val vm = VolleyManager(this)
        vm.call(
            url,
            { response ->
            val m = makeMeme(response.toString())
            setFAB(m)
            },
            { response ->
            vm.fail(this, response.toString(), binding.mainContent.textView, binding.mainContent.button)
            }
        )
    }

    // handles closing the app
    override fun onDestroy() {
        dbManager.close()
        super.onDestroy()
    }
}