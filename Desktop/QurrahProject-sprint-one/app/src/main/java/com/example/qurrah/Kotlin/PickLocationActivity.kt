package com.example.qurrah.Kotlin
import android.app.Activity
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.vanillaplacepicker.presentation.builder.VanillaPlacePicker
import com.vanillaplacepicker.utils.KeyUtils
import com.vanillaplacepicker.utils.MapType
import com.vanillaplacepicker.utils.PickerLanguage
import com.vanillaplacepicker.utils.PickerType

class PickLocationActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = VanillaPlacePicker.Builder(this)
                .with(PickerType.MAP)
                .setMapType(MapType.SATELLITE)
                .setPickerLanguage(PickerLanguage.ARABIC)
                .setMapPinDrawable(com.example.qurrah.R.drawable.ic_location)
                .build()
        startActivityForResult(intent, KeyUtils.REQUEST_PLACE_PICKER)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                KeyUtils.REQUEST_PLACE_PICKER -> {
                    val vanillaAddress =
                            VanillaPlacePicker.onActivityResult(requestCode, resultCode, data)
                    vanillaAddress?.let {
                        val intent = Intent()

                        intent.putExtra("Latitude",it.latitude.toString())
                        intent.putExtra("Longitude", it.longitude.toString())
                        intent.putExtra("Address",  it.formattedAddress?.replace("""[0-9]""".toRegex(),""))
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                }
                }
            }
        }
        finish()
    }
}
