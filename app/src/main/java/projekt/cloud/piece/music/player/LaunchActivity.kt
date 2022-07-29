package projekt.cloud.piece.music.player

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import projekt.cloud.piece.music.player.databinding.ActivityLaunchBinding
import projekt.cloud.piece.music.player.databinding.LayoutLaunchBinding

class LaunchActivity: AppCompatActivity() {
    
    private val textList = listOf(
        Pair(R.string.launch_title_read, R.string.launch_content_read),
        Pair(R.string.launch_title_write, R.string.launch_content_write)
    )
    
    private inner class ViewPagerHolder(private val binding: LayoutLaunchBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(pair: Pair<Int, Int>) {
            binding.materialTextViewTitle.setText(pair.first)
            binding.materialTextViewContent.setText(pair.second)
            when (viewPager2.currentItem) {
                0 -> requestReadPermission()
                else -> requestWritePermission()
            }
        }
    }
    
    private inner class ViewPager2Adapter: RecyclerView.Adapter<ViewPagerHolder>() {
    
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewPagerHolder(
            LayoutLaunchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    
        override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
            holder.bindData(textList[position])
        }
    
        override fun getItemCount() = textList.size
        
    }
    
    private lateinit var binding: ActivityLaunchBinding
    private val root get() = binding.root
    private val viewPager2: ViewPager2
        get() = binding.viewPager2
    
    private lateinit var requestPermission: ActivityResultLauncher<String>
    private var requestRespondAction: (Boolean) -> Unit = {
        if (it) {
            when {
                SDK_INT >= Q -> launchMainActivity()
                else -> requestWritePermission()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            requestRespondAction.invoke(it)
        }
        
        when {
            checkReadPermission() -> {
                if (checkWritePermission() || SDK_INT >= Q) {
                    splashScreen.setKeepOnScreenCondition { true }
                    launchMainActivity()
                    return finish()
                }
                setupViewPager2(1)
                requestWritePermission()
            }
            else -> {
                setupViewPager2(0)
                requestReadPermission()
            }
        }
    }
    
    private fun launchMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    
    private fun checkReadPermission() =
        ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
    
    private fun checkWritePermission() =
        ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED
    
    private fun setupViewPager2(position: Int) {
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(root)
        viewPager2.adapter = ViewPager2Adapter()
        viewPager2.isUserInputEnabled = false
        viewPager2.currentItem = position
    }
    
    private fun requestReadPermission() =
        requestPermission.launch(READ_EXTERNAL_STORAGE)
    
    private fun requestWritePermission() {
        viewPager2.currentItem = 1
        requestRespondAction = {
            if (it) {
                launchMainActivity()
            }
        }
        requestPermission.launch(WRITE_EXTERNAL_STORAGE)
    }
    
}