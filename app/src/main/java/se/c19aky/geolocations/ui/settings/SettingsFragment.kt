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

/**
 * Fragment for viewing the settings, which currently is just a single button
 */
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null

    private lateinit var deleteAllLocations: Button

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /**
     * Setup the delete all locations button
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        deleteAllLocations = binding.btnDeleteAllLocations

        deleteAllLocations.setOnClickListener {
            settingsViewModel.locationListLiveData.observe(viewLifecycleOwner) {
                locations -> locations?.let {
                    settingsViewModel.deleteLocations(locations)
                    settingsViewModel.locationListLiveData.removeObservers(viewLifecycleOwner)
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