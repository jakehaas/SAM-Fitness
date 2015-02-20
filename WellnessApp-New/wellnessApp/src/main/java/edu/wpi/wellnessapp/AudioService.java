package edu.wpi.wellnessapp;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;



public class AudioService extends Service {
    private static final String TAG = "AudioService";
    MediaRecorder mRecorder;
    SensorManager sensorMgr = null;
    Sensor lightSensor = null;
    float lightIntensity;

    Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        timer = new Timer();

        String mFileName = Utils.getFilePath(this);

        //Set up light sensor
        sensorMgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        lightSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_LIGHT);

        //Set up media recorder
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }

    SensorEventListener lightlsn = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            lightIntensity = event.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }
    };

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Sleep Tracking Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        timer.cancel();
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        Toast.makeText(this, "Sleep Tracking Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("FailedStart", "prepare() failed");
        }

        mRecorder.start();
        sensorMgr.registerListener(lightlsn, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        sampleAudio();

        return START_NOT_STICKY;
    }

    private void sampleAudio()
    {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent("NewMessage");
                i.putExtra("maxAmplitude", Integer.toString(mRecorder.getMaxAmplitude()));
                i.putExtra("lightIntensity", Float.toString(lightIntensity));
                sendBroadcast(i);
            }
        }, 0, 1000);


    }
}
