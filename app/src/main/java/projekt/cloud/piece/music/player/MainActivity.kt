package projekt.cloud.piece.music.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import projekt.cloud.piece.music.player.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    
    private lateinit var binding: ActivityMainBinding
    
    private val root get() = binding.root

    private val fragmentContainerView get() = binding.fragmentContainerView
    
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(root)
        
        navController = fragmentContainerView.getFragment<NavHostFragment>().navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
    }
    
    override fun onSupportNavigateUp() =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    
}