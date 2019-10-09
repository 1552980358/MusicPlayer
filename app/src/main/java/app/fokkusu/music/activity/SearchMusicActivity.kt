package app.fokkusu.music.activity

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import app.fokkusu.music.base.activity.BaseAppCompatActivity
import app.fokkusu.music.R
import app.fokkusu.music.service.PlayService
import kotlinx.android.synthetic.main.activity_search.listMusicView
import kotlinx.android.synthetic.main.activity_search.toolbar

/**
 * @File    : SearchMusicActivity
 * @Author  : 1552980358
 * @Date    : 8 Oct 2019
 * @TIME    : 5:56 PM
 **/

class SearchMusicActivity : BaseAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.navigationIcon!!.setTint(Color.BLACK)
        
        listMusicView.setUpAdapterWithMusicList(mutableListOf(), 1)
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_toolbar, menu)
        (menu!!.findItem(R.id.searchView).actionView as SearchView).apply {
            
            (this::class.java.getDeclaredField("mSearchSrcTextView").apply {
                isAccessible = true
            }.get(this) as AutoCompleteTextView).setTextColor(Color.BLACK)
            
            onActionViewExpanded()
            isSubmitButtonEnabled = false
            
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }
                
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText == null || newText.isEmpty()) {
                        listMusicView.apply {
                            updateMusic(getMusicList().apply { clear() })
                        }
                        return false
                    }
                    
                    val list = listMusicView.getMusicList().apply { clear() }
                    for (i in PlayService.musicList) {
                        if (i.title().contains(query)) {
                            list.add(i)
                        }
                    }
                    listMusicView.updateMusic(list)
                    
                    return true
                }
            })
        }
        
        return super.onCreateOptionsMenu(menu)
    }
}