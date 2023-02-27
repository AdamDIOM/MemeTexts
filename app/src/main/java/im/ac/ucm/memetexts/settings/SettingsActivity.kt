package im.ac.ucm.memetexts.settings

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import im.ac.ucm.memetexts.R
import im.ac.ucm.memetexts.databinding.ActivitySettingsBinding
import im.ac.ucm.memetexts.main.MainActivity


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferenceManager: SharedPreferences
    private lateinit var originalPersonalSubreddit: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // inflates binding and sets content view
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // instantiates preference manager and gets personal subreddit at launch
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
        originalPersonalSubreddit = preferenceManager.getString("extra_meme", "")!!

        // replaces content with settings fragment
        supportFragmentManager.beginTransaction().replace(binding.settingsContent.root.id, SettingsFragment()).commit()

        // sets menu/toolbar
        setSupportActionBar(binding.toolbar)
    }

    /**Inflates menu bar and adds menu items to toolbar.*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dbview, menu)
        return true
    }

    /**Switches through different menu item options to navigate to different activities.*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // when 'Return' option is selected,
            R.id.action_return -> {
                // creates Intent and puts extra "changed_personalised" true if it has changed, false if not
                val intent = Intent(this, MainActivity::class.java)
                val prefString = preferenceManager.getString("extra_meme", "")
                intent.putExtra("changed_personalised", prefString != originalPersonalSubreddit)
                // sets activity as successful and finishes, returning "changed_personalised"
                setResult(Activity.RESULT_OK, intent)
                finish()
                true
            }
            // if the options are not listed here
            else -> super.onOptionsItemSelected(item)
        }
    }
}