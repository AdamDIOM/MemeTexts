package im.ac.ucm.memetexts.main

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.PreferenceManager
import im.ac.ucm.memetexts.*
import im.ac.ucm.memetexts.databinding.ActivityMainBinding
import im.ac.ucm.memetexts.settings.SettingsActivity

class MainActivity : AppCompatActivity(), GetMemeFragment.Caller {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbManager: DbManager
    private lateinit var gmFragment: GetMemeFragment
    private lateinit var preferenceManager: SharedPreferences
    private var changed = false

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

        // assumes that spinner custom option has not changed by default
        changed = false

        gmFragment = supportFragmentManager.findFragmentByTag("get_meme_fragment_tag") as GetMemeFragment

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
    }

    /**Handles launching ViewDB and getting a response*/
    private val launchViewDB = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK){
            val sub = result.data?.getStringExtra("sub")
            if(!sub.isNullOrEmpty()){
                gmFragment.buttonClick(sub)
            }
        }
    }
    /**Handles launching settings and getting a response*/
    private val launchSettings = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val changedSub = result.data?.getBooleanExtra("changed_personalised", false)
            changed = changedSub!!
        }
    }

    /**Gets the available options for the spinner in Fragment, including potential custom subreddit
     * @return an ArrayAdapter of items available to be used in a Spinner*/
    override fun getSpinnerOptions(): SpinnerAdapter {
        var array = resources.getStringArray(R.array.spinner_options).toMutableList()
        val eSub = arrayOf(preferenceManager.getString("extra_meme", "")!!).toMutableList()
        if(eSub[0] != ""){
            if (changed){
                eSub.addAll(array)
                array = eSub
            }
            else{
                array.addAll(eSub)
            }
        }

        return ArrayAdapter(this, android.R.layout.simple_spinner_item, array)
    }

    /**Handles creation of options menu in toolbar*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**Switches through different menu item options to navigate to different activities.
     * @return whether the selection was successful*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_view_db -> {
                val intent = Intent(this, DBViewActivity::class.java)
                launchViewDB.launch(intent)
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                launchSettings.launch(intent)
                true
            }
            // if the options are not listed here
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**Sends the chosen API to the Fragment
     * @return API link*/
    override fun getUrl(): String{
        return "https://meme-api.com/gimme/"
    }

    /**Handles successful API call. Creates a meme and sets the FAB to handle favouring this meme
     * @param response the JSON response from the API
     * @param tView the TextView to show the meme title
     * @param button the Button to enable after the success*/
    override fun successResponse(response: String, tView: TextView, button: Button) {
        val m = makeMeme(response, tView, button)
        setFAB(m)
    }

    /**Handles failure of API call.
     * @param response the JSON response from the API
     * @param tView the TextView to alert the user of the error
     * @param button the Button to enable after the failure
     * @param vm the VolleyManager that handles the API calls*/
    override fun failResponse(response: String, tView: TextView, button: Button, vm: VolleyManager) {
        vm.fail(this, response, tView, button)
    }

    /**Creates instance of Meme class from API string response, checks if it is safe for work and posts to the page (or generates a new one).
     * @param response the JSON response from the API
     * @param tView the TextView to display the meme title
     * @param button the Button to enable after the success
     * @return a meme object*/
    private fun makeMeme(response: String, tView: TextView, button: Button): Meme {

        val m = Meme(response)
        if(m.isNsfw() && !preferenceManager.getBoolean("allow_nsfw", false)){
            button.callOnClick()
        }
        else{
            m.postImageButton(tView, binding.mainContent.imageView, button)
        }
        return m
    }

    /** sets the FAB's onClick to add the current meme to the database and show a Snackbar output
     * @param m meme to favourite*/
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

    /**Closes the database connection when the activity ends*/
    override fun onDestroy() {
        dbManager.close()
        super.onDestroy()
    }

}