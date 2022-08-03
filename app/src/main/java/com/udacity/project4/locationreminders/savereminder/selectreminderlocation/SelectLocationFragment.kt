package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.isLocationPermissionGranted
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@SuppressLint("MissingPermission")
class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    private lateinit var map: GoogleMap
    private var marker: Marker? = null
    override val _viewModel: SaveReminderViewModel by sharedViewModel()
    private lateinit var binding: FragmentSelectLocationBinding

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 123
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)

        val mapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.saveButton.setOnClickListener {
            if (marker != null) {
                onLocationSelected()
            } else {
                _viewModel.showToast.value = getString(R.string.select_location)
            }
        }

        return binding.root
    }

    private fun onLocationSelected() {
        marker?.let {
            _viewModel.saveLocation(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    @TargetApi(29)
    private fun enableMyLocation() {
        if (isLocationPermissionGranted(requireContext())) {
            _viewModel.showToast.value = getString(R.string.select_location)

            map.isMyLocationEnabled = true
            setLocationListener()
        } else {
            requestPermissions(
                arrayOf(
                    ACCESS_FINE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            } else {
                _viewModel.showErrorDialog.value =
                    getString(R.string.permission_denied_explanation_on_select_location)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(googleMaps: GoogleMap) {
        map = googleMaps
        enableMyLocation()
        setPoiClick()

        map.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.map_style_json
            )
        )
    }

    private fun setPoiClick() {
        map.setOnPoiClickListener { poi ->
            if (marker != null) {
                marker?.remove()
            }

            marker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            marker?.showInfoWindow()
        }

        map.setOnMapClickListener { latLng ->
            if (marker != null) {
                marker?.remove()
            }

            val title = getString(R.string.lat_long_snippet, latLng.latitude, latLng.longitude)
            marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(title)
            )
            marker?.showInfoWindow()
        }
    }


    private fun setLocationListener() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            val currentLocation = task.result
            currentLocation?.let {
                val zoomLevel = 15f
                val homeLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
            }
        }
    }
}
