package uz.umarxon.map5

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import uz.umarxon.map5.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = "MainActivity"
    val REQUEST_CODE_PERMISSION = 1000
    lateinit var locationRequest: LocationRequest
    private lateinit var geocoder: Geocoder

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var mMap:GoogleMap? = null
    var marker:Marker? = null

    var locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            for (loc:Location in p0.locations){
                Log.d("Murodhonov",loc.toString())

                if (mMap != null){
                    marker = mMap!!.addMarker(MarkerOptions().position(LatLng(loc.latitude,loc.longitude)))
                    //val camera = CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude,loc.longitude),15.0f)
                    //mMap!!.animateCamera(camera)
                    val camera = CameraPosition.Builder()
                        .target(marker!!.position)
                        .bearing(loc.bearing)
                        .tilt(70.0f)
                        .zoom(17.0f)
                        .build()
                    mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(camera))

                }
            }
        }
    }

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map1) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
        locationRequest.interval = 350
        locationRequest.fastestInterval = 350
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        geocoder = Geocoder(this, Locale.getDefault())







    }

    override fun onStart() {
        super.onStart()

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            checkSettingsAndStartUpdates()
        }else{
            askLocationPermission()
        }
    }

    fun askLocationPermission() {
        Log.d(TAG, "askLocationPermission: siz dialog chiqarishingiz mumkin")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE_PERMISSION
        )
    }

    override fun onStop() {
        super.onStop()

        stopLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    fun checkSettingsAndStartUpdates() {
        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()
        val client = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask: Task<LocationSettingsResponse> =
            client.checkLocationSettings(request)
        locationSettingsResponseTask.addOnSuccessListener {
            //Settings of device are satisfied and we can start location updates
            startLocationUpdates()
        }
        locationSettingsResponseTask.addOnFailureListener {
            Log.d(TAG, "checkSettingsAndStartUpdates: Error")
            Toast.makeText(this, "Xatolik \ncheckSettingsAndStartUpdates", Toast.LENGTH_SHORT)
                .show()
            openGpsEnableSetting()
        }
    }

    val REQUEST_ENABLE_GPS = 1
    private fun openGpsEnableSetting() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, REQUEST_ENABLE_GPS)
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
    }

}