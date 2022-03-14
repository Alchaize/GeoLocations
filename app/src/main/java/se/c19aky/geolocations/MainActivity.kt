package se.c19aky.geolocations

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import se.c19aky.geolocations.databinding.ActivityMainBinding
import se.c19aky.geolocations.ui.dashboard.DashboardFragment
import se.c19aky.geolocations.ui.dashboard.DashboardFragmentDirections
import se.c19aky.geolocations.ui.location.LocationFragment
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), DashboardFragment.Callbacks {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_maps, R.id.navigation_dashboard, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onLocationSelected(locationId: UUID) {
        val id = locationId.toString()
        val directions = DashboardFragmentDirections.actionNavigationDashboardToLocationFragment(id)
        navController.navigate(directions)
    }
}