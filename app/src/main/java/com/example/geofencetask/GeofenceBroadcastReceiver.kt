package com.example.geofencetask

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import com.google.android.gms.location.Geofence

import com.google.android.gms.location.GeofencingEvent




class GeofenceBroadcastReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent!!.hasError()) {
            return
        }


        when (geofencingEvent.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
              Log.d("GeoFence","Transition Enter")
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {

                Log.d("GeoFence","Transition Dwell")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {

                Log.d("GeoFence","Transition Exit")
            }
        }
    }

}
