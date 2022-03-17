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

        mapsViewModel.locationListLiveData.observe(viewLifecycleOwner
        ) { locations ->
            locations?.let {
                for (location in locations) {
                    val pos = LatLng(location.latitude, location.longitude)
                    googleMap.addMarker(MarkerOptions().position(pos).title(location.name))
                }
            }
        }

        mapsViewModel.currentLocation.observe(viewLifecycleOwner
        ) { location ->
            location?.let {
                fusedLocationClient.removeLocationUpdates(locationCallback)

                googleMap.addMarker(MarkerOptions().position(location).title("Me"))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10F))
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations){
                    mapsViewModel.currentLocation.value = LatLng(location.latitude, location.longitude)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(mapCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_map, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.save_location -> {

                val location = Location()
                mapsViewModel.addLocation(location)

                mapsViewModel.currentLocation.observe(viewLifecycleOwner
                ) { currentLocation ->
                    currentLocation.let {
                        location.latitude = currentLocation.latitude
                        location.longitude = currentLocation.longitude
                        mapsViewModel.currentLocation.removeObservers(viewLifecycleOwner)
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
}