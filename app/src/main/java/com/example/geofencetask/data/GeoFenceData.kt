package com.example.geofencetask.data

import com.google.android.gms.maps.model.LatLng

data class GeoFenceData(
    val latLng: LatLng,
    val geoFenceRadius: Float,
    val geoFenceId: String,
    val pendingIntentRequestCode: Int,
)