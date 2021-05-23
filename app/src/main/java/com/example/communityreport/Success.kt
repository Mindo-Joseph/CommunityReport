package com.example.communityreport

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_success.*
import java.util.*
import java.util.jar.Manifest


class Success : AppCompatActivity() {

    var placesClient: PlacesClient? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        // Create a new Places client instance.

        // Create a new Places client instance.
        placesClient = Places.createClient(this)
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment!!.setTypeFilter(TypeFilter.CITIES)
        autocompleteFragment.setPlaceFields(
            Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.PHOTO_METADATAS
            )
        )

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(@NonNull place: Place) {
                // TODO: Get info about the selected place.
                Toast.makeText(applicationContext, place.name, Toast.LENGTH_SHORT).show()
                val photoRequest: FetchPhotoRequest = FetchPhotoRequest.builder(
                    Objects.requireNonNull(place.photoMetadatas).get(0)
                )
                    .build()
                placesClient!!.fetchPhoto(photoRequest).addOnSuccessListener(
                    object : OnSuccessListener<FetchPhotoResponse?> {


                        override fun onSuccess(p0: FetchPhotoResponse?) {
                            val bitmap: Bitmap = p0!!.bitmap
                            (findViewById<ImageView>(R.id.img)).setImageBitmap(bitmap)
                        }
                    })
                    .addOnFailureListener(object : OnFailureListener {
                        override fun onFailure(@NonNull exception: Exception) {
                            exception.printStackTrace()
                        }
                    })
            }

            override fun onError(p0: Status) {
                Toast.makeText(applicationContext, p0.toString(), Toast.LENGTH_SHORT)
                    .show()
            }


        })
        setupLocationClient()
        getCurrentLocation()
    }


    private fun setupLocationClient(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
    // prompt the user to grant/deny access
    private fun requestLocPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), //permission in the manifest
            REQUEST_LOCATION)
    }

    companion object {
        private const val REQUEST_LOCATION = 1 //request code to identify specific permission request
        private const val TAG = "Success Activity" // for debugging
    }

    private fun getCurrentLocation() {
        // Check if the ACCESS_FINE_LOCATION permission was granted before requesting a location
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            // If the permission has not been granted, then requestLocationPermissions() is called.
            requestLocPermissions()
        } else {

            fusedLocationClient.lastLocation.addOnCompleteListener {
                // lastLocation is a task running in the background
                val location = it.result //obtain location
                //Get a reference to the database, so your app can perform read and write operations
                val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                val ref: DatabaseReference = database.getReference("locations")
                if (location != null) {
                    ref.setValue(location)
                } else {
                    // if location is null , log an error message
                    Log.e(TAG, "No location found")
                }



            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        //check if the request code matches the REQUEST_LOCATION
        if (requestCode == REQUEST_LOCATION)
        {
            //check if grantResults contains PERMISSION_GRANTED.If it does, call getCurrentLocation()
            if (grantResults.size == 1 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                //if it doesn`t log an error message
                Log.e(TAG, "Location permission denied")
            }
        }
    }




}


