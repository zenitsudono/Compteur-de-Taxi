package com.example.taxi

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.example.taxi.databinding.ActivityDriverProfileBinding
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.google.zxing.BarcodeFormat
import android.graphics.Bitmap
import android.util.Log
import java.util.Locale
import android.content.res.Configuration
import android.content.Intent
import com.example.taxi.models.Driver
import com.google.gson.Gson

class DriverProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverProfileBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var currentDriver: Driver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        loadLocale()
        
        binding = ActivityDriverProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadDriverInfo()
        setupLanguageSpinner()
        setupDriverInfo()
        generateQRCode()
    }

    private fun loadDriverInfo() {
        // In a real app, this would come from Firebase or your backend
        currentDriver = Driver(
            id = "D123",
            name = "Mohammed Hassan",
            age = 35,
            licenseType = "Type B",
            baseFare = 2.5,
            perKmRate = 1.5,
            perMinuteRate = 0.5,
            rating = 4.8f,
            phoneNumber = "+212 6XX-XXXXXX",
            carModel = "Dacia Logan",
            carPlateNumber = "12345-A-7"
        )
    }

    private fun loadLocale() {
        val languageCode = prefs.getString("language_code", "en") ?: "en"
        setLocale(languageCode)
    }

    private fun setupLanguageSpinner() {
        val languages = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        binding.languageSpinner.adapter = adapter

        val currentLang = prefs.getString("language_code", "en") ?: "en"
        val position = when (currentLang) {
            "en" -> 0
            "fr" -> 1
            "ar" -> 2
            else -> 0
        }
        binding.languageSpinner.setSelection(position)

        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val languageCode = when (position) {
                    0 -> "en"
                    1 -> "fr"
                    2 -> "ar"
                    else -> "en"
                }
                if (languageCode != currentLang) {
                    setLocale(languageCode)
                    restartActivity()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        
        resources.updateConfiguration(config, resources.displayMetrics)
        prefs.edit().putString("language_code", languageCode).apply()
    }

    private fun restartActivity() {
        val intent = Intent(this, DriverProfileActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun setupDriverInfo() {
        binding.apply {
            tvDriverName.text = currentDriver.name
            tvAge.text = getString(R.string.driver_age) + ": ${currentDriver.age}"
            tvLicenseType.text = getString(R.string.license_type) + ": ${currentDriver.licenseType}"
            tvBaseFare.text = getString(R.string.base_fare) + ": ${currentDriver.baseFare} DH"
            tvPerKm.text = getString(R.string.per_km) + ": ${currentDriver.perKmRate} DH"
            tvPerMinute.text = getString(R.string.per_minute) + ": ${currentDriver.perMinuteRate} DH"
            tvPhoneNumber.text = getString(R.string.phone_number) + ": ${currentDriver.phoneNumber}"
            tvCarInfo.text = getString(R.string.car_info) + ": ${currentDriver.carModel} (${currentDriver.carPlateNumber})"
            ratingBar.rating = currentDriver.rating
        }
    }

    private fun generateQRCode() {
        try {
            val driverInfo = Gson().toJson(currentDriver)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                driverInfo,
                BarcodeFormat.QR_CODE,
                400,
                400
            )
            binding.ivQrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Log.e("QRCode", "Error generating QR code", e)
        }
    }
}
