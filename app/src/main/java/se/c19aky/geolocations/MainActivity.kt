package se.c19aky.geolocations

import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import se.c19aky.geolocations.databinding.ActivityMainBinding
import se.c19aky.geolocations.ui.list.ListFragment
import se.c19aky.geolocations.ui.list.ListFragmentDirections
import se.c19aky.geolocations.ui.maps.MapsFragment
import se.c19aky.geolocations.ui.maps.MapsFragmentDirections
import java.util.*


private const val TAG = "MainActivity"

/**
 * MainActivity
 * Switches between the fragments, controls the navbar and asks for location permission.
 */
class MainActivity : AppCompatActivity(), ListFragment.Callbacks, MapsFragment.Callbacks {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            } else -> {
            // No location access granted.
            Snackbar.make(this.binding.container, "Location permission makes creating locations easier", Snackbar.LENGTH_INDEFINITE)
                .setAction("No thanks"){
                    Snackbar.make(this.binding.container, "Ok", Snackbar.LENGTH_SHORT).show()
                }.show()
            }
        }
    }


    /**
     * Create main activity
     */
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
                R.id.navigation_maps, R.id.navigation_list, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        askForLocationPermission()
    }

    /**
     * Move user to location fragment when they select a location
     */
    override fun onLocationSelected(locationId: UUID) {
        val id = locationId.toString()
        val directions = ListFragmentDirections.actionNavigationListToNavigationLocation(id)
        navController.navigate(directions)
    }

    /**
     * Move user to location fragment when they create a new location
     */
    override fun newLocationCreated(locationId: UUID) {
        val id = locationId.toString()
        val directions = MapsFragmentDirections.actionNavigationMapsToNavigationLocation(id)
        navController.navigate(directions)
    }

    /**
     * Check if location permission is given
     */
    override fun isLocationPermissionGiven(): Boolean {
        return ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Ask user for permission to use the location of the phone
     */
    private fun askForLocationPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION
            ) -> {
                // You can use the API that requires the permission.
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                locationPermissionRequest.launch(
                    arrayOf(permission.ACCESS_FINE_LOCATION,
                        permission.ACCESS_COARSE_LOCATION))
            }
        }
    }

}