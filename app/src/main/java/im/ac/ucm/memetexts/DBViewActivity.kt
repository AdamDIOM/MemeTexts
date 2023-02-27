package im.ac.ucm.memetexts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import im.ac.ucm.memetexts.databinding.ActivityDbviewBinding

class DBViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDbviewBinding
    private lateinit var memeAdapter: MemeAdapter
    private lateinit var dbM: DbManager

    /**Sets up binding, connects to database and retrieves all, removes NSFW memes if disallowed, instantiates RecyclerView for displaying memes.*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDbviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbM = DbManager(this)
        val memesList = dbM.retrieve() as MutableList<Meme>

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
        if(!preferenceManager.getBoolean("allow_nsfw", false)) {
            memesList.removeAll { m ->
                m.isNsfw()
            }
        }

        setSupportActionBar(binding.toolbar)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        memeAdapter = MemeAdapter(this)
        binding.recyclerView.adapter = memeAdapter
        (binding.recyclerView.adapter as MemeAdapter).memes = memesList
        (binding.recyclerView.adapter as MemeAdapter).notifyDataSetChanged()
    }

    /**Inflates the toolbar and menu*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dbview, menu)
        return true
    }

    /**Switches through different menu item options to navigate to different activities.*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_return -> {
                finish()
                true
            }
            // if the options are not listed here
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**Closes the database connection when the activity is finished.*/
    override fun onDestroy() {
        dbM.close()
        super.onDestroy()
    }

    /**Notifies the RecyclerView that the data has changed when the activity restarts.*/
    public override fun onRestart() {
        super.onRestart()
        (binding.recyclerView.adapter as MemeAdapter).notifyDataSetChanged()
    }
}