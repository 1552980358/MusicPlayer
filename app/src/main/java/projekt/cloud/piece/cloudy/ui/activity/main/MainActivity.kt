package projekt.cloud.piece.cloudy.ui.activity.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import projekt.cloud.piece.cloudy.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {

    companion object MainActivityUtil {

        private val mainActivityClass: Class<MainActivity>
            get() = MainActivity::class.java

        val Context.mainActivityIntent: Intent
            get() = Intent(this, mainActivityClass)

    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        navController = ActivityMainBinding.inflate(layoutInflater).let { binding ->
            setContentView(binding)
            findNavController(binding)
        }
    }

    private fun setContentView(binding: ActivityMainBinding) {
        setContentView(binding.root)
    }

    private fun findNavController(binding: ActivityMainBinding): NavController {
        return binding.fragmentContainerView
            .getFragment<NavHostFragment>()
            .navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}