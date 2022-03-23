package se.c19aky.geolocations.ui.location

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import se.c19aky.geolocations.Location
import se.c19aky.geolocations.R
import se.c19aky.geolocations.databinding.FragmentLocationBinding
import java.util.*

private const val TAG = "LocationFragment"

/**
 * Fragment for viewing a single location
 */
class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args : LocationFragmentArgs by navArgs()
    private lateinit var location: Location
    private lateinit var nameField: EditText
    private lateinit var latitudeField: EditText
    private lateinit var longitudeField: EditText
    private lateinit var descriptionField: EditText


    private lateinit var deleteButton: Button

    private var saveLocation = true

    // Get ViewModel
    private val locationViewModel: LocationViewModel by lazy {
        ViewModelProvider(this)[LocationViewModel::class.java]
    }

    /**
     * Create text watchers for each EditText
     */
    override fun onStart() {
        super.onStart()

        // Watch the location name field
        val nameWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                location.name = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        // Watch the latitude field
        val latitudeWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    location.latitude = p0.toString().toDouble()
                } catch (e: NumberFormatException) {
                    // Don't update on invalid input
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        // Watch the longitude field
        val longitudeWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    location.longitude = p0.toString().toDouble()
                } catch (e: NumberFormatException) {
                    // Don't update on invalid input
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        // Watch the description field
        val descriptionWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                location.description = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        nameField.addTextChangedListener(nameWatcher)
        latitudeField.addTextChangedListener(latitudeWatcher)
        longitudeField.addTextChangedListener(longitudeWatcher)
        descriptionField.addTextChangedListener(descriptionWatcher)
    }

    /**
     * Get each component
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        location = Location()

        _binding = FragmentLocationBinding.inflate(inflater, container, false)

        nameField = binding.textLocation
        latitudeField = binding.textLocationLatitude
        longitudeField = binding.textLocationLongitude
        descriptionField = binding.textLocationDescription
        deleteButton = binding.btnDelete

        setupDeleteButton()

        return binding.root
    }

    /**
     * Load the data of the location given by the args when starting this fragment
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationViewModel.loadLocation(UUID.fromString(args.id))

        locationViewModel.locationLiveData.observe(viewLifecycleOwner
        ) { location ->
            location?.let {
                this.location = location
                updateUI()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Update location data before leaving unless the location is to be deleted if the location
     */
    override fun onStop() {
        super.onStop()
        if (saveLocation) {
            locationViewModel.saveLocation(location)
        }
    }

    /**
     * Update the information in each text field
     */
    private fun updateUI() {
        nameField.setText(location.name)
        latitudeField.setText(location.latitude.toString())
        longitudeField.setText(location.longitude.toString())
        descriptionField.setText(location.description)
    }

    /**
     * Setup the delete button
     */
    private fun setupDeleteButton() {
        deleteButton.setBackgroundColor(Color.RED)

        deleteButton.setOnClickListener {
            saveLocation = false
            locationViewModel.removeLocation(location)
            this.requireActivity().onBackPressed()
        }
    }
}