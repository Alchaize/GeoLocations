package se.c19aky.geolocations.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.c19aky.geolocations.Location
import se.c19aky.geolocations.R
import se.c19aky.geolocations.databinding.FragmentDashboardBinding
import java.util.*

private const val TAG = "DashboardFragment"

class DashboardFragment : Fragment() {

    /**
     * Required interface for hosting activities
     */
    interface Callbacks {
        fun onLocationSelected(locationId: UUID)
    }

    private var callbacks: Callbacks? = null

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var locationRecyclerView: RecyclerView
    private var adapter: LocationAdapter? = LocationAdapter(emptyList())

    private val dashboardViewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        locationRecyclerView = root as RecyclerView
        locationRecyclerView.layoutManager = LinearLayoutManager(context)
        locationRecyclerView.adapter = adapter

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashboardViewModel.locationListLiveData.observe(viewLifecycleOwner
        ) { locations ->
            locations?.let {
                updateUI(locations)
            }
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

    private fun updateUI(locations: List<Location>) {
        adapter = LocationAdapter(locations)
        locationRecyclerView.adapter = adapter
    }

    private inner class LocationHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var location: Location

        private val locationName : TextView = itemView.findViewById(R.id.location_title)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(location: Location) {
            this.location = location
            locationName.text = location.name
        }

        override fun onClick(p0: View?) {
            callbacks?.onLocationSelected(location.id)
        }
    }

    private inner class LocationAdapter(var locations: List<Location>) : RecyclerView.Adapter<LocationHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
            val view = layoutInflater.inflate(R.layout.list_item_location, parent, false)
            return LocationHolder(view)
        }

        override fun getItemCount(): Int = locations.size

        override fun onBindViewHolder(holder: LocationHolder, position: Int) {
            val location = locations[position]
            holder.bind(location)
        }
    }
}