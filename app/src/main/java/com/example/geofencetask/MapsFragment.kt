package com.example.geofencetask

import android.Manifest
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.geofencetask.data.GeoFenceData
import com.example.geofencetask.utils.GeoFenceUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment() {

    private lateinit var map: GoogleMap
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceUtil: GeoFenceUtil

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        map = googleMap
        val bangalore = LatLng(12.9716, 77.5946)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangalore, 16F))

        enableLocation()

        initGeoFence()

    }

    private fun enableLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true;
        } else {


            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initUI()
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    private fun initGeoFence() {
        val geoFenceListNew = ArrayList<Geofence>()
        geoFenceList.forEach {
            addMarker(it.latLng)
            addCircle(it.latLng, it.geoFenceRadius)
            val geofence: Geofence = geofenceUtil.getGeofence(
                it.geoFenceId,
                it.latLng,
                it.geoFenceRadius,
                Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
            )
            geoFenceListNew.add(geofence)
        }

        addGeofence(geoFenceListNew)

    }

    private fun addGeofence(geofenceList: ArrayList<Geofence>) {

        val geofencingRequest: GeofencingRequest = geofenceUtil.getGeofencingRequest(geofenceList)
        val pendingIntent: PendingIntent? = geofenceUtil.getPendingIntent()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (pendingIntent != null) {
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener {
                    Log.d("geoSuccess","geoSuccess")
                }
                .addOnFailureListener { e ->
                    Log.d("geoFail", "geoFail$e")
                    val errorMessage: String? = geofenceUtil.getErrorString(e)
                }
        }
    }

    private fun initUI() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(
                        requireContext(),
                        "Permission Not granted",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        map.isMyLocationEnabled = true;
                    }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        geofencingClient = LocationServices.getGeofencingClient(requireContext())
        geofenceUtil = GeoFenceUtil(requireContext())
    }

    private fun addMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng)
        map.addMarker(markerOptions)
    }

    private fun addCircle(latLng: LatLng, radius: Float) {
        val circleOptions = CircleOptions()
        circleOptions.center(latLng)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions.strokeWidth(4f)
        map.addCircle(circleOptions)
    }

    companion object {
        val geoFenceList = arrayListOf(
            GeoFenceData(LatLng(12.953013054035946, 77.5417514266668), 100F, "geo_fence_id_1",2000),
            GeoFenceData(LatLng(12.95428866232216, 77.5438757362066), 100F, "geo_fence_id_2",2001),
            GeoFenceData(LatLng(12.95558517552543, 77.54565672299249), 100F, "geo_fence_id_3",2002),
            GeoFenceData(LatLng(12.956442543452548, 77.54752354046686), 100F, "geo_fence_id_4",2003),
            GeoFenceData(LatLng(12.95675621390793, 77.54919723889215), 100F, "geo_fence_id_5",2004),
            GeoFenceData(LatLng(12.957069883968225, 77.5511284293828), 100F, "geo_fence_id_6",2005),
            GeoFenceData(LatLng(12.957711349517467, 77.55308710458465), 100F, "geo_fence_id_7",2006),
            GeoFenceData(LatLng(12.958464154110917, 77.55514704110809), 100F, "geo_fence_id_8",2007),
            GeoFenceData(LatLng(12.95965609006252, 77.5559409749765), 100F, "geo_fence_id_9",2008),
            GeoFenceData(LatLng(12.960814324441305, 77.5574167738546), 100F, "geo_fence_id_10",2009),
            GeoFenceData(LatLng(12.961253455257907, 77.5592192183126), 100F, "geo_fence_id_11",2010),
            GeoFenceData(LatLng(12.96156308861349, 77.56126500049922), 100F, "geo_fence_id_12",2011),

            GeoFenceData(LatLng(12.96181401984877, 77.56308890262935), 100F, "geo_fence_id_13",2012),
            GeoFenceData(LatLng(12.962775920574138, 77.56592131534907), 100F, "geo_fence_id_14",2013),
            GeoFenceData(LatLng(12.96334051274693, 77.567637929118), 100F, "geo_fence_id_15",2014),
            GeoFenceData(LatLng(12.9641111467689, 77.56951526792183), 100F, "geo_fence_id_16",2015),
            GeoFenceData(LatLng(12.96438298614629, 77.5717254081501), 100F, "geo_fence_id_17",2016),
            GeoFenceData(LatLng(12.964584624077109, 77.57370388966811), 100F, "geo_fence_id_18",2017),
            GeoFenceData(LatLng(12.964542802687657, 77.57591402989638), 100F, "geo_fence_id_19",2018),
            GeoFenceData(LatLng(12.963795326167373, 77.57798997552734), 100F, "geo_fence_id_20",2019),
            GeoFenceData(LatLng(12.96358621848664, 77.58015720041138), 100F, "geo_fence_id_21",2020),
            GeoFenceData(LatLng(12.963481664580394, 77.58189527185303), 100F, "geo_fence_id_22",2021),
        )
    }
}