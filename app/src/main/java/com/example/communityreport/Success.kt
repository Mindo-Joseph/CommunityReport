package com.example.communityreport

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
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
import kotlinx.android.synthetic.main.activity_success.*
import java.util.*


class Success : AppCompatActivity() {

    var placesClient: PlacesClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
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
    }


    }


