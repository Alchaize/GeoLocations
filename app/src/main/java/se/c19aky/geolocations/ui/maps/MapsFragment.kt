package se.c19aky.geolocations.ui.maps

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import se.c19aky.geolocations.Location
import se.c19aky.geolocations.R
import se.c19aky.geolocations.databinding.FragmentMapsBinding
import se.c19aky.geolocations.ui.dashboard.DashboardFragment
import se.c19aky.geolocations.ui.dashboard.DashboardViewModel
import java.util.*

private const val TAG = "MapsFragment"

class MapsFragment : Fragment() {

    /**
     * Required interface for hosting activities
     */
    interface Callbacks {
        fun newLocationCreated(locationId: UUID)
        fun isLocationPermissionGiven(): Boolean
    }

    private var callbacks: Callbacks? = null

    private var _binding: FragmentMapsBinding? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mapsViewModel: MapsViewModel by lazy {
        ViewModelProvider(this)[MapsViewModel::class.java]
    }

    private val mapCallback = OnMapReadyCallback { googleMap ->

        if (mapsViewModel.locationPermissionGiven) {
            mapsViewModel.currentLocation.observe(viewLifecycleOwner
            ) { location ->
                location?.let {
                    // Zoom in on the user
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 8F))

                    // We don't want to zoom in again every time the current location is updated
                    mapsViewModel.currentLocation.removeObservers(viewLifecycleOwner)
                    mapsViewModel.currentLocation.observe(viewLifecycleOwner)
                    { location ->
                        location?.let {
                            mapsViewModel.redrawMarkers.value = true
                        }
                    }
                }
            }
        }


        mapsViewModel.redrawMarkers.observe(viewLifecycleOwner) {
            value ->
            value?.let {
                if (value) {
                    mapsViewModel.redrawMarkers.value = false
                    googleMap.clear()

                    // Draw current location
                    if (mapsViewModel.locationPermissionGiven) {
                        mapsViewModel.currentLocation.value?.let { it1 ->
                            MarkerOptions().position(it1).title("Me")
                        }?.let { it2 -> googleMap.addMarker(it2) }
                    }

                    // Draw the locations in the database
                    for (location in mapsViewModel.locations) {
                        val pos = LatLng(location.latitude, location.longitude)
                        googleMap.addMarker(MarkerOptions().position(pos).title(location.name))
                    }
                }
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapsBinding.inflate(inflater, container, false)

        if (callbacks?.isLocationPermissionGiven() == true) {
            mapsViewModel.locationPermissionGiven = true

            subscribeToLocationUpdates()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(mapCallback)

        mapsViewModel.locationListLiveData.observe(viewLifecycleOwner
        ) { locations ->
            locations?.let {
                mapsViewModel.locations.clear()
                mapsViewModel.locations.addAll(locations)
                mapsViewModel.redrawMarkers.value = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_map, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.save_location -> {

                // Create new location
                val location = Location()
                mapsViewModel.addLocation(location)

                // Use current location if possible
                if (mapsViewModel.locationPermissionGiven) {
                    mapsViewModel.currentLocation.observe(viewLifecycleOwner
                    ) { currentLocation ->
                        currentLocation.let {
                            location.latitude = currentLocation.latitude
                            location.longitude = currentLocation.longitude
                            mapsViewModel.currentLocation.removeObservers(viewLifecycleOwner)
                        }
                    }
                }

                callbacks?.newLocationCreated(location.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        locationRequest = createLocationRequest()
        locationCallback = createLocationCallback()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun createLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                mapsViewModel.currentLocation.value = LatLng(location.latitude, location.longitude)
            }
        }
    }
}