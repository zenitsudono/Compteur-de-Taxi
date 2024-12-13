package com.example.taxi

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.taxi.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.text.DecimalFormat
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityMainBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isRideStarted = false
    private var startTime = 0L
    private var lastLocation: Location? = null
    private var totalDistance = 0.0
    private val handler = Handler(Looper.getMainLooper())
    private var locationCallback: LocationCallback? = null

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
        private const val BASE_FARE = 2.5
        private const val PER_KM_RATE = 1.5
        private const val PER_MINUTE_RATE = 0.5
        private const val NOTIFICATION_CHANNEL_ID = "taxi_meter_channel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMap()
        setupLocationServices()
        createNotificationChannel()
        setupClickListeners()
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupClickListeners() {
        binding.btnStartRide.setOnClickListener {
            if (!isRideStarted) {
                startRide()
            } else {
                endRide()
            }
        }

        binding.fabProfile.setOnClickListener {
            startActivity(Intent(this, DriverProfileActivity::class.java))
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRide() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestLocationPermission()
            return
        }

        isRideStarted = true
        startTime = SystemClock.elapsedRealtime()
        binding.btnStartRide.text = getString(R.string.end_ride)
        binding.btnStartRide.setBackgroundColor(getColor(R.color.red))

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(1000)
            .setMaxUpdateDelayMillis(2000)
            .build()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
        startTimeUpdates()
    }

    private fun endRide() {
        isRideStarted = false
        binding.btnStartRide.text = getString(R.string.start_ride)
        binding.btnStartRide.setBackgroundColor(getColor(R.color.green))
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        handler.removeCallbacksAndMessages(null)
        showRideCompletionNotification()
    }

    private fun updateLocation(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        map.clear()
        map.addMarker(MarkerOptions().position(currentLatLng))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))

        lastLocation?.let { last ->
            val distance = location.distanceTo(last) / 1000 // Convert to kilometers
            totalDistance += distance
            updateDistanceAndFare()
        }
        lastLocation = location
    }

    private fun startTimeUpdates() {
        handler.post(object : Runnable {
            override fun run() {
                if (isRideStarted) {
                    updateTimeAndFare()
                    handler.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun updateTimeAndFare() {
        val elapsedMillis = SystemClock.elapsedRealtime() - startTime
        val minutes = (elapsedMillis / 60000).toInt()
        val seconds = ((elapsedMillis % 60000) / 1000).toInt()
        binding.tvTime.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        updateFare(minutes.toLong())
    }

    private fun updateDistanceAndFare() {
        val df = DecimalFormat("#.##")
        binding.tvDistance.text = "${df.format(totalDistance)} km"
        updateFare((SystemClock.elapsedRealtime() - startTime) / 60000)
    }

    private fun updateFare(minutes: Long) {
        val fare = BASE_FARE + (totalDistance * PER_KM_RATE) + (minutes * PER_MINUTE_RATE)
        val df = DecimalFormat("#.##")
        binding.tvFare.text = "${df.format(fare)} DH"
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Taxi Meter"
            val descriptionText = "Taxi meter notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showRideCompletionNotification() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_person)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(
                R.string.notification_message,
                binding.tvFare.text,
                binding.tvDistance.text,
                binding.tvTime.text
            ))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    @AfterPermissionGranted(PERMISSION_REQUEST_CODE)
    private fun requestLocationPermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            startRide()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_rationale),
                PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = true
            isCompassEnabled = true
        }
        enableMyLocation()
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            map.isMyLocationEnabled = true
            startLocationUpdates()
            
            // Get last known location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(10000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { location ->
                    updateLocation(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            enableMyLocation()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // Handle permission denied
    }

    override fun onDestroy() {
        super.onDestroy()
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        handler.removeCallbacksAndMessages(null)
    }
}