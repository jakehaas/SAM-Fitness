package edu.wpi.wellnessapp;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class AudioService extends Service {
	private static final String TAG = "AudioService";
	MediaRecorder mRecorder;
	
	String mFileName = Utils.getFilePath(this);
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");

		//player = MediaPlayer.create(this, R.raw.braincandy);
		//player.setLooping(false); // Set looping
		
		onRecord(true);
	}
	
	public void onRecord(boolean start) {
        if (start) {
            startAudioRecording();
            //startService(new Intent(this, AudioService.class));
        } else {
            stopAudioRecording();
            //stopService(new Intent(this, AudioService.class));
        }
    }	
    
    //starts audio recording
    private void startAudioRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("FailedStart", "prepare() failed");
        }

        mRecorder.start();
    }
    //stops audio recording
    private void stopAudioRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
    
    //returns current audio level
    private Integer getAudioLevel(){
    	return mRecorder.getMaxAmplitude();
    }

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		//player.stop();
		onRecord(false);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		//player.start();
		onRecord(true);
	}

}
