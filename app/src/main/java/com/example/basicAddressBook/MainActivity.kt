package com.example.basicAddressBook
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.basicAddressBook.database.ContactListTable
import com.example.basicAddressBook.database.DatabaseHelper
import com.example.basicAddressBook.database.PrefHelper
import com.example.basicAddressBook.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

// The starting activity for the app. Here the main activity is started, along with the drawer navigation
// toolbar, database, and user shared preferences.

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize drawer navigation and toolbar
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_contact_list, R.id.nav_create_contact), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        // Initialize database, check to upgrade
        val dataHelper = DatabaseHelper.getInstance(this)
        val prefHelper = PrefHelper(this)
        val savedDatabaseVersion = prefHelper.loadInt(PrefHelper.KEY_DATABASE_VERSION)
        if (savedDatabaseVersion < DatabaseHelper.DATABASE_VERSION) {
            // database outdated, clear user preferences and upgrade sql database
            dataHelper.doUpgrade()
            prefHelper.clearSharedPreference()
            prefHelper.save(PrefHelper.KEY_DATABASE_VERSION, DatabaseHelper.DATABASE_VERSION)
            prefHelper.save(PrefHelper.KEY_PREFERRED_SORTING_METHOD, ContactListTable.CUSTOMER_ID_COL)
            prefHelper.save(PrefHelper.KEY_PREFER_JSON, false)
        }
        dataHelper.initAllTables(this)

        // get permissions
        val permissionState = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION), REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getLocation()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        DatabaseHelper.getInstance(this).close()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_contact_list) {
            drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(R.id.nav_contact_list)
            return true
        }
        else if (item.itemId == R.id.nav_create_contact) {
            drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(R.id.nav_create_contact)
            return true
        }

        return false
    }
}