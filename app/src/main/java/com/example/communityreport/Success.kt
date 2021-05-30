package com.example.communityreport


import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class Success : AppCompatActivity() {

    var placesClient: PlacesClient? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var lastDeviceLocation: Location? = null

    //Get a reference to the database, so your app can perform read and write operations
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var uploadBtn: Button? = null
    private var chooseBtn: Button? = null
    private var imageView: ImageView? = null
    private var photoDescription: EditText? = null



    //vars
    private val root = FirebaseDatabase.getInstance().getReference("Image")
    private val reference = FirebaseStorage.getInstance().reference
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        uploadBtn = findViewById(R.id.upload_btn);
        imageView = findViewById(R.id.imageView);
        chooseBtn = findViewById(R.id.ButtonChooseImage);
        photoDescription = findViewById(R.id.PhotoDescription);




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }


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



        chooseBtn?.setOnClickListener(View.OnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 2)
        })

        uploadBtn?.setOnClickListener(View.OnClickListener {
            if (imageUri != null) {
                uploadToFirebase(imageUri!!)
            } else {
                Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show()
            }
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            imageView!!.setImageURI(imageUri)
        }
    }


    private fun setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    // prompt the user to grant/deny access
    private fun requestLocPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), //permission in the manifest
            REQUEST_LOCATION
        )
    }

    companion object {
        private const val REQUEST_LOCATION =
            1 //request code to identify specific permission request
        private const val TAG = "Success Activity" // for debugging
    }

    private fun getCurrentLocation() {
        // Check if the ACCESS_FINE_LOCATION permission was granted before requesting a location
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            // If the permission has not been granted, then requestLocationPermissions() is called.
            requestLocPermissions()
        } else {

            fusedLocationClient.lastLocation.addOnCompleteListener {
                // lastLocation is a task running in the background
                val location = it.result //obtain location



                if (location != null) {
                    lastDeviceLocation = location
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
        grantResults: IntArray
    ) {
        //check if the request code matches the REQUEST_LOCATION
        if (requestCode == REQUEST_LOCATION) {
            //check if grantResults contains PERMISSION_GRANTED.If it does, call getCurrentLocation()
            if (grantResults.size == 1 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                getCurrentLocation()
            } else {
                //if it doesn`t log an error message
                Log.e(TAG, "Location permission denied")
            }
        }
    }

    private fun uploadToFirebase(uri: Uri) {
        val fileRef =
            reference.child(System.currentTimeMillis().toString() + "." + getFileExtension(uri))
        val imageDescription = photoDescription?.getText().toString().trim();
        fileRef.putFile(uri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                val model = Model(uri.toString(),imageDescription,lastDeviceLocation)
                val modelId = root.push().key
                root.child(modelId!!).setValue(model)


                Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this, UploadSuccess::class.java)



                startActivity(intent)

            }
        }.addOnFailureListener {
            Toast.makeText(this, "Uploading Failed !!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileExtension(mUri: Uri): String? {
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(mUri))
    }


}


