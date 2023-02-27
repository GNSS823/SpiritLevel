package com.example.spiritlevel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private ImageView imageView;
    private TextView textViewAccelX, textViewAccelY, textViewAccelZ;
    private TextView textViewPitch, textViewRoll;

    private float[] accelerometerData = new float[3];
    private float pitch, roll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a reference to the views
        imageView = (ImageView) findViewById(R.id.imageView);
        textViewAccelX = (TextView) findViewById(R.id.text_view_accel_x);
        textViewAccelY = (TextView) findViewById(R.id.text_view_accel_y);
        textViewAccelZ = (TextView) findViewById(R.id.text_view_accel_z);
        textViewPitch = (TextView) findViewById(R.id.text_view_pitch);
        textViewRoll = (TextView) findViewById(R.id.text_view_roll);

        // Get a reference to the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Get a reference to the accelerometer
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the sensor listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the sensor listener
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Get the sensor type
        int sensorType = event.sensor.getType();

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            accelerometerData = event.values.clone();
            calculatePitchAndRoll();

            float pitchInDegrees = (float) Math.toDegrees(pitch);
            float rollInDegrees = (float) Math.toDegrees(roll);

            // Update the UI
            textViewAccelX.setText(String.format("%.2f", accelerometerData[0]));
            textViewAccelY.setText(String.format("%.2f", accelerometerData[1]));
            textViewAccelZ.setText(String.format("%.2f", accelerometerData[2]));
            textViewPitch.setText(String.format("%.2f°", pitchInDegrees));
            textViewRoll.setText(String.format("%.2f°", rollInDegrees));
            moveImageView();
        }
    }

    private void calculatePitchAndRoll() {
        float ax = accelerometerData[0];
        float ay = accelerometerData[1];
        float az = accelerometerData[2];

        pitch = (float) Math.atan2(-ay, Math.sqrt(ax*ax + az*az));
        roll = (float) Math.atan2(ax, Math.sqrt(ay*ay + az*az));
    }

    private void moveImageView() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        int imageWidth = imageView.getWidth();
        int imageHeight = imageView.getHeight();

        int centerX = screenWidth / 2 - imageWidth / 2;
        int centerY = screenHeight / 2 - imageHeight / 2;

        int targetX = (int) (centerX + roll * screenWidth / 2);
        int targetY = (int) (centerY + pitch * screenHeight / 2);

        // Make sure the image doesn't go off the screen
        if (targetX < 0) {
            targetX = 0;
        } else if (targetX + imageWidth > screenWidth) {
            targetX = screenWidth - imageWidth;
        }
        if (targetY < 0) {
            targetY = 0;
        } else if (targetY + imageHeight > screenHeight) {
            targetY = screenHeight - imageHeight;
        }

        imageView.setX(targetX);
        imageView.setY(targetY);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}