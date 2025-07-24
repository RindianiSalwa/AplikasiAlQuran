package com.example.aplikasialquran

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragmentContainer: FrameLayout

    private var homeFragment: HomeFragment? = null
    private var historyFragment: HistoryFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Al-Quran"

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        fragmentContainer = findViewById(R.id.fragmentContainer)

        if (savedInstanceState == null) {
            homeFragment = HomeFragment()
            homeFragment?.let { replaceFragment(it, "HomeFragmentTag") }
        } else {
            homeFragment = supportFragmentManager.findFragmentByTag("HomeFragmentTag") as? HomeFragment
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (homeFragment == null) {
                        homeFragment = HomeFragment()
                    }
                    homeFragment?.let { replaceFragment(it, "HomeFragmentTag") }
                    true
                }
                R.id.navigation_favorite -> {
                    replaceFragment(FavoriteFragment(), "FavoriteFragmentTag")
                    true
                }
                R.id.navigation_history -> {
                    if (historyFragment == null) {
                        historyFragment = HistoryFragment()
                        Log.d(TAG, "Navigasi History: HistoryFragment diinisialisasi baru.")
                    }
                    replaceFragment(historyFragment!!, "HistoryFragmentTag")
                    Log.d(TAG, "Navigasi History: Menampilkan HistoryFragment.")
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.navigation_home
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment, tag)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Cari surah..."

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                bottomNavigationView.visibility = View.GONE
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                bottomNavigationView.visibility = View.VISIBLE
                homeFragment?.filterSurah(null)
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                homeFragment?.filterSurah(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                homeFragment?.filterSurah(newText)
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_about_us) {
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
