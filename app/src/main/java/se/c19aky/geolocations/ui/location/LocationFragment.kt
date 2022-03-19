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

    private val locationViewModel: LocationViewModel by lazy {
        ViewModelProvider(this)[LocationViewModel::class.java]
    }

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
                val result = p0.toString()
                if (result.isNotEmpty()) {
                    location.latitude = result.toDouble()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        // Watch the longitude field
        val longitudeWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val result = p0.toString()
                if (result.isNotEmpty()) {
                    location.longitude = result.toDouble()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        // Watch the longitude field
        val descriptionWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val result = p0.toString()
                if (result.isNotEmpty()) {
                    location.description = result
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        nameField.addTextChangedListener(nameWatcher)
        latitudeField.addTextChangedListener(latitudeWatcher)
        longitudeField.addTextChangedListener(longitudeWatcher)
        descriptionField.addTextChangedListener(descriptionWatcher)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        location = Location()

        _binding = FragmentLocationBinding.inflate(inflater, container, false)

        nameField = binding.root.findViewById(R.id.text_location)
        latitudeField = binding.root.findViewById(R.id.text_location_latitude)
        longitudeField = binding.root.findViewById(R.id.text_location_longitude)
        descriptionField = binding.root.findViewById(R.id.text_location_description)
        deleteButton = binding.root.findViewById(R.id.btn_delete)

        setupDeleteButton()

        return binding.root
    }

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

    override fun onStop() {
        super.onStop()
        if (saveLocation) {
            locationViewModel.saveLocation(location)
        }
    }

    private fun updateUI() {
        nameField.setText(location.name)
        latitudeField.setText(location.latitude.toString())
        longitudeField.setText(location.longitude.toString())
        descriptionField.setText(location.description)
    }

    private fun setupDeleteButton() {
        deleteButton.setBackgroundColor(Color.RED)

        deleteButton.setOnClickListener {
            saveLocation = false
            locationViewModel.removeLocation(location)
            this.requireActivity().onBackPressed()
        }
    }
}