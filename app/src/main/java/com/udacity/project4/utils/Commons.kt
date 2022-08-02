package com.udacity.project4.utils

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat

fun isLocationEnabled(context: Context): Boolean {
    val locationService = context.getSystemService(LOCATION_SERVICE) as LocationManager
    return locationService.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun isPermissionGranted(context: Context) = ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED