package com.example.geofencetask.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import com.example.geofencetask.GeofenceBroadcastReceiver
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng


class GeoFenceUtil(base: Context): ContextWrapper(base) {
    private var pendingIntent: PendingIntent? = null
    fun getGeofencingRequest(geofences: ArrayList<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder()
            .addGeofences(geofences)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
    }

    fun getGeofence(ID: String, latLng: LatLng, radius: Float, transitionTypes: Int): Geofence {
        return Geofence.Builder()
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setRequestId(ID)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(5000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun getPendingIntent(): PendingIntent? {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        if(pendingIntent != null) return pendingIntent
        pendingIntent = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, 2000, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        else{
            PendingIntent.getBroadcast(this, 2000, intent, PendingIntent.FLAG_MUTABLE)
        }
        return pendingIntent
    }

    fun getErrorString(e: Exception): String? {
        if (e is ApiException) {
            when (e.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> return "GEOFENCE_NOT_AVAILABLE"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> return "GEOFENCE_TOO_MANY_GEO_FENCES"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> return "GEOFENCE_TOO_MANY_PENDING_INTENTS"
            }
        }
        return e.localizedMessage
    }
}