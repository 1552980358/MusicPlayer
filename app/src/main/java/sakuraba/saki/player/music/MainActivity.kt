package sakuraba.saki.player.music

import android.os.Bundle
import android.view.Menu
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import sakuraba.saki.player.music.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    
    private var _activityMainMainBinding: ActivityMainBinding? = null
    private val activityMain get() = _activityMainMainBinding!!
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityMainMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMain.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home), activityMain.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        activityMain.navView.setupWithNavController(navController)
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    
    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    
    override fun onDestroy() {
        super.onDestroy()
        _activityMainMainBinding = null
    }
}