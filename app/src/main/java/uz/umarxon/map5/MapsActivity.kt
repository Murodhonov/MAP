package uz.umarxon.map5

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import uz.umarxon.map5.databinding.ActivityMapsBinding
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var fusedLocatedProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocatedProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    var marker: Marker? = null
    //var list = ArrayList<LatLng>()

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(40.383114011480565, 71.78271019770168)
        marker = mMap.addMarker(MarkerOptions().position(sydney).title("Marker in codial"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f))

        mMap.setOnMapLongClickListener {
            mMap.addMarker(MarkerOptions().position(it).title("${it.toString()}"))
        }

        binding.btn1.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }

        binding.btn2.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }

        binding.btn3.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }

        binding.btn4.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_NONE
        }

        binding.btn5.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        /*mMap.setOnMapClickListener {
            marker?.position = it
            val camera = CameraUpdateFactory.newLatLngZoom(it,8.0f)
            mMap.animateCamera(camera)
            Toast.makeText(this, "${it.toString()}", Toast.LENGTH_SHORT).show()
        }*/

        /*val polyLine = mMap.addPolygon(PolygonOptions()
                .clickable(true)
                .add(
                    LatLng(-35.016, 143.321),
                    LatLng(-35.747, 145.891)
                )
        )*/

        /*val polyLine = mMap.addPolyline(PolylineOptions()
            .clickable(true)
            .addAll(list)
            .color(Color.RED)
        )

        mMap.setOnMapClickListener {
            list.add(it)
            polyLine.points = list
        }*/

        /*list.add(LatLng(-35.747, 145.891))

        val polygon = mMap.addPolygon(PolygonOptions()
            .addAll(list)
            .clickable(true)
            .fillColor(Color.GREEN))

        mMap.setOnMapClickListener {
            list.add(it)
            polygon.points = list
        }
        mMap.setOnPolygonClickListener {
            Toast.makeText(this, "Clicked polygon", Toast.LENGTH_SHORT).show()
        }*/

        deviceLocation()

        mMap.setOnMapClickListener {
            Toast.makeText(this, getAddressFromLatLng(this, LatLng(it.latitude,it.longitude)), Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("MissingPermission")
    fun deviceLocation(){
        val locationTask: Task<Location> = fusedLocatedProviderClient.lastLocation
        locationTask.addOnSuccessListener {
            if (it!=null){
                Log.d("Murodhonov", "deviceLocation: ${it.toString()}")

                mMap.addMarker(MarkerOptions()
                    .position
                        (LatLng(it.latitude,it.longitude)))

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude,it.longitude),10.0f))

                Toast.makeText(this, getAddressFromLatLng(this, LatLng(it.latitude,it.longitude)), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getAddressFromLatLng(context: Context?, latLng: LatLng): String? {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(context, Locale.getDefault())
        return try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addresses[0].getAddressLine(0)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}