package com.example.marblerollapp

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs


class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var gravitySensor: Sensor

    private var x = mutableFloatStateOf(0f)
    private var y = mutableFloatStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)!!

        enableEdgeToEdge()
        setContent {
            MarbleScreen(x,y)
        }
    }

    override fun onResume() {
        super.onResume()
        gravitySensor.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GRAVITY){
            x.floatValue -= if (abs( event.values[0])>0.1f) event.values[0] else 0f
            y.floatValue += if (abs(event.values[1])>0.1f) event.values[1] else 0f
            Log.d(null, "event values ${event.values[0]}, ${event.values[1]}, ${event.values[2]}")
            Log.d(null, "x value - ${x.floatValue} and y value - ${y.floatValue}")
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}

private fun getScreenDpi(context: Context): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    return displayMetrics.densityDpi
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarbleScreen(x: MutableFloatState, y: MutableFloatState) {
    Scaffold (
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Marble Rolling"
                    )
                }
            )
        }
    ){ innerPadding ->
        Column (modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                text = "Rolling Area"
            )

            Spacer(modifier = Modifier.padding(16.dp))

            BoxWithConstraints (
            modifier = Modifier
                .background(color = Color.LightGray)
                .widthIn(min = 400.dp, max = 400.dp)
                .heightIn(min = 400.dp, max = 400.dp)
                .clipToBounds()
            ) {

                val circleRadius = 20.dp.value

                val dpi = getScreenDpi(LocalContext.current)

                val maxh = maxHeight.value * (dpi)/160f
                val maxw = maxWidth.value * (dpi/160f)


                if (x.floatValue + circleRadius< 0f){
                    x.floatValue = 0f + circleRadius
                }

                if (x.floatValue  + circleRadius > maxw){
                    x.floatValue  = maxw - circleRadius
                }

                if (y.floatValue + circleRadius < 0f){
                    y.floatValue = 0f + circleRadius
                }

                if (y.floatValue + circleRadius > maxh){
                    y.floatValue = maxh - circleRadius
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.Red,
                        radius = circleRadius,
                        center = Offset(x.floatValue, y.floatValue)
                    )
                }
            }

        }
    }
}