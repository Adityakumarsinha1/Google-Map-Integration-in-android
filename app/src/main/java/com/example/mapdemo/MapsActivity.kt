package com.example.mapdemo

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.get

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mapdemo.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback , GoogleMap.OnMarkerClickListener{

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMapsBinding

    companion object{
        private const val LOCATION_REQUEST_CODE=1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        Log.d("@@@","map loaded")
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)


//        implementing search view and extracting the queries for searching them
        val locationSearch: SearchView=findViewById(R.id.searchLocation)

        locationSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            @SuppressLint("SuspiciousIndentation")
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty())
                    locationSearch(query.toString())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d("@@@","map loaded1")
       mMap.uiSettings.isZoomControlsEnabled=true
       mMap.setOnMarkerClickListener(this)
        setUPMap()
    }

    private fun setUPMap() {

        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED  )
        {
            Log.d("@@@","permisson  not granted")
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
            return
        }

        mMap.isMyLocationEnabled=true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location->

            if (location!=null){
                lastLocation=location
                val currentLatLocation = LatLng(location.latitude,location.longitude)
                placeMarkerOnMap(currentLatLocation)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLocation,15f))
                Log.d("@@@","longlat=  $currentLatLocation")
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLocation: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLocation)
        markerOptions.title("$currentLatLocation")
        mMap.addMarker(markerOptions)
        Log.d("@@@","adding marker")
    }

    override fun onMarkerClick(p0: Marker)= false

//    for searching location

    private fun locationSearch(location: String) {

        var addressList: List<Address>? = null

        if (location == null || location == ""){
            Toast.makeText(this, "provide location", Toast.LENGTH_SHORT).show()
        }else{
            val geoCoder = Geocoder(this)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)
            }catch (e: IOException){
                e.printStackTrace()
            }

            val address = addressList!![0]
            val latLng = LatLng(address.latitude, address.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title(location))
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }
}