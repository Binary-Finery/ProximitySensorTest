package com.spencer_studios.proximitysensortest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var proximitySensor: Sensor
    private lateinit var lottieView: LottieAnimationView
    private lateinit var mediaPlayer: MediaPlayer

    private var color: Int = 0
    private var count: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        toolbar.visibility = View.GONE
        lottieView = findViewById(R.id.lottie_not_found)

        playAnimation(false)
        setEventCount()

        ivReset.setOnClickListener {
            count = 0
            setEventCount()
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        sensorManager.registerListener(
            proximitySensorEventListener,
            proximitySensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        setSensorInfo()
    }

    private var proximitySensorEventListener: SensorEventListener =
        object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                //unused method
            }

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_PROXIMITY)
                    playAnimation(event.values[0] < event.sensor.maximumRange)
            }
        }

    private fun playAnimation(objectDetected: Boolean) {
        if (objectDetected) {
            mediaPlayer.start()
            color = Color.parseColor("#4CAF50")
            tv.text = getString(R.string.object_detected)
            lottieView.setAnimation("thumbs_up.json")
            count++
            setEventCount()
        } else {
            color = Color.parseColor("#F44336")
            tv.text = getString(R.string.no_object_detected)
            lottieView.setAnimation("not_found_anim.json")
        }

        lottieView.playAnimation()
        parentLayout.setBackgroundColor(color)
        window.apply {
            statusBarColor = color
            navigationBarColor = color
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setSensorInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                tvInfo.text =
                            "name       : ${proximitySensor.name}\n" +
                            "vendor     : ${proximitySensor.vendor}\n" +
                            "power      : ${proximitySensor.power} mA\n" +
                            "resolution : ${proximitySensor.resolution}"
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setEventCount() {
        tvEvents.text = "EVENTS $count"
    }


    override fun onStart() {
        super.onStart()
        mediaPlayer = MediaPlayer.create(this, R.raw.chime_1)
        sensorManager.registerListener(
            proximitySensorEventListener,
            proximitySensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
        mediaPlayer.release()
        sensorManager.unregisterListener(proximitySensorEventListener)
    }
}