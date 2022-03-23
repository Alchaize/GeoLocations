package se.c19aky.geolocations.ui.list

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.c19aky.geolocations.Location
import se.c19aky.geolocations.R
import se.c19aky.geolocations.databinding.FragmentListBinding
import java.util.*

private const val TAG = "ListFragment"

/**
 * Fragment for looking at all locations in a long list, with each entry being clickable
 */
class ListFragment : Fragment() {

    /**
     * Required interface for hosting activities
     */
    interface Callbacks {
        fun onLocationSelected(locationId: UUID)
    }

    private var callbacks: Callbacks? = null

    private var _binding: FragmentListBinding? = null
    private lateinit var locationRecyclerView: RecyclerView
    private var adapter: LocationAdapter? = LocationAdapter(emptyList())

    // Get ViewModel
    private val listViewModel: ListViewModel by lazy {
        ViewModelProvider(this)[ListViewModel::class.java]
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    /**
     * Get components in layout
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        locationRecyclerView = root as RecyclerView
        locationRecyclerView.layoutManager = LinearLayoutManager(context)
        locationRecyclerView.adapter = adapter

        return root
    }

    /**
     * Start to observe the livedata in ViewModel to update UI when it has changed
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listViewModel.locationListLiveData.observe(viewLifecycleOwner
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

    /**
     * Update the UI, i.e. the list of locations
     */
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