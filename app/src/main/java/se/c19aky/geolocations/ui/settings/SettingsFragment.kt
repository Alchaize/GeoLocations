package se.c19aky.geolocations.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import se.c19aky.geolocations.databinding.FragmentSettingsBinding

private const val TAG = "SettingsFragment"

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null

    private lateinit var askPermission: Button
    private lateinit var deleteAllLocations: Button

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            } else -> {
            // No location access granted.
        }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        askPermission = binding.btnAskPermissions
        deleteAllLocations = binding.btnDeleteAllLocations

        askPermission.setOnClickListener {
            Log.d(TAG, "Asking for permission...")
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) -> {
                    // You can use the API that requires the permission.
                    Toast.makeText(this.requireContext(), "You have already granted permission", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    locationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION))
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}