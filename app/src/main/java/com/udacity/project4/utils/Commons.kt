package com.udacity.project4.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

fun isLocationPermissionGranted(context: Context) = ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED

@RequiresApi(Build.VERSION_CODES.Q)
fun isBackgroundLocationPermissionGranted(context: Context) = ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION
) == PackageManager.PERMISSION_GRANTED