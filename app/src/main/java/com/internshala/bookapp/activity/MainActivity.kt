package com.internshala.bookapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.internshala.bookapp.R
import com.internshala.bookapp.fragment.AboutAppFragment
import com.internshala.bookapp.fragment.DashboardFragment
import com.internshala.bookapp.fragment.FavoritesFragment
import com.internshala.bookapp.fragment.ProfileFragment

lateinit var toolbar: Toolbar
lateinit var drawerLayout: DrawerLayout
lateinit var navigationView: NavigationView
lateinit var coordinatorLayout: CoordinatorLayout
lateinit var frameLayout: FrameLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(
            R.id.toolbar
        )
        drawerLayout = findViewById(
            R.id.drawer_layout
        )
        navigationView = findViewById(
            R.id.navigation_view
        )
        coordinatorLayout = findViewById(
            R.id.coordinator_layout
        )
        frameLayout = findViewById(
            R.id.frame_layout
        )

        setUpToolBar()
        openDashboard()

        val actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            it.isCheckable = true
            it.isChecked = true

            when(it.itemId){
                R.id.dashboard -> {
                    openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.favorites -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame_layout,
                        FavoritesFragment()
                    ).commit()
                    supportActionBar?.title = "Favorites"
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame_layout,
                        ProfileFragment()
                    ).commit()
                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.about_app -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame_layout,
                        AboutAppFragment()
                    ).commit()
                    supportActionBar?.title = "About App"
                    drawerLayout.closeDrawers()
                }
            }

            return@setNavigationItemSelectedListener true
        }

    }

    private fun setUpToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openDashboard(){
        supportFragmentManager.beginTransaction().replace(
            R.id.frame_layout,
            DashboardFragment()
        ).commit()
        supportActionBar?.title = "Dashboard"
        navigationView.setCheckedItem(
            R.id.dashboard
        )
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame_layout)
        when(frag){
            !is DashboardFragment -> openDashboard()
            else -> super.onBackPressed()
        }

    }

}