package se.c19aky.geolocations.ui.location

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private val locationViewModel: LocationViewModel by lazy {
        ViewModelProvider(this)[LocationViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()

        val nameWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Blank
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                location.name = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                // Blank
            }
        }

        nameField.addTextChangedListener(nameWatcher)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        location = Location()

        _binding = FragmentLocationBinding.inflate(inflater, container, false)

        nameField = binding.root.findViewById(R.id.text_location)

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
        locationViewModel.saveLocation(location)
    }

    private fun updateUI() {
        nameField.setText(location.name)
    }
}