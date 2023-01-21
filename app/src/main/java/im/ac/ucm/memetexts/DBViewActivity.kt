package im.ac.ucm.memetexts

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import im.ac.ucm.memetexts.databinding.ActivityDbviewBinding

class DBViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDbviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDbviewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_dbview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)

        return when (item.itemId) {
            R.id.action_return -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}