package com.example.projectcllinic


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle





import android.view.Menu
import android.view.MenuItem
import android.view.View

import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity()  {


    lateinit var toggle: ActionBarDrawerToggle
    lateinit var bottomnavigation: BottomNavigationView
    private lateinit var navController: NavController
    lateinit var drawerlayoutt : NavigationView
    private lateinit var binding : MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(binding.toolbarrr)

        bottomnavigation = findViewById(R.id.bottom_navigation)
        var user = FirebaseAuth.getInstance().currentUser

        bottomnavigation.background = null
        bottomnavigation.menu.getItem(1).isEnabled = false
        val first = HomeFragment()
        val second = AddFragment()
        setCurrentFragment(first)
        bottomnavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home_nav -> setCurrentFragment(first)
                R.id.chat_nav -> setCurrentFragment(RecentChatFragment())
            }
            true
        }
        val fab: View = findViewById(R.id.add_nav)
        fab.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, second).addToBackStack(null).commit()
            }

        }


    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.nav_profile -> { startActivity(Intent(this,profileUpdate::class.java))
            }
            R.id.nav_logout -> {

                val fAuth = FirebaseAuth.getInstance()
                fAuth.signOut()
                startActivity(Intent(this,LoginActivity::class.java))
            }
            R.id.about -> { startActivity(Intent(this,ActivityAbout::class.java))
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    override fun onBackPressed() {
        //  super.onBackPressed();
        moveTaskToBack(true)
    }

}
