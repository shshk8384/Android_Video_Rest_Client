package com.monllao.david.androidrestclient;

import com.monllao.david.androidrestclient.receiver.AddServerVideoReceiver;
import com.monllao.david.androidrestclient.receiver.PutServerVideoReceiver;
import com.monllao.david.androidrestclient.service.AddServerVideoService;
import com.monllao.david.androidrestclient.service.PutServerVideoService;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class VideoDataActivity extends Activity {

	private AddServerVideoReceiver addVideoReceiver;
	private PutServerVideoReceiver putVideoReceiver;
	
	// Form elements
	private EditText titleText;
	private Button confirmButton;
	private boolean submitted = false;

	private Video video;
	
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_data);

        // Registering receivers
        IntentFilter addvideofilter = new IntentFilter(AndroidRestClientActivity.ACTION_ADDVIDEO);
        addVideoReceiver = new AddServerVideoReceiver();
        registerReceiver(addVideoReceiver, addvideofilter);

        IntentFilter putvideofilter = new IntentFilter(AndroidRestClientActivity.ACTION_PUTVIDEO);
        putVideoReceiver = new PutServerVideoReceiver();
        registerReceiver(putVideoReceiver, putvideofilter);
        
        // Add the video as soon as possible
        addVideo();
        
        // Form elements
        titleText = (EditText) findViewById(R.id.title);
        confirmButton = (Button) findViewById(R.id.set_video_data);    
        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

            	submitted = true;
            	
            	// The video id has been received so send the petition
            	if (video != null) {
            		video.setName(titleText.getText().toString());
            		putVideo();
            	}
            }

        });
    }
    
    
    public void onDestroy() {
    	super.onDestroy();
    	
    	// Unregistering the broadcast receivers
    	unregisterReceiver(addVideoReceiver);
    	unregisterReceiver(putVideoReceiver);
    }
    
    
    /**
     * Finish the application
     */
    public void onBackPressed() {
    	setResult(RESULT_CANCELED);
    	finish();
    }
    

    /**
     * Starts the service the update the video
     */
    protected void putVideo() {

    	Intent intent = new Intent(this, PutServerVideoService.class);
    	intent.setAction(AndroidRestClientActivity.ACTION_PUTVIDEO);
    	
    	intent.putExtra("video", video);
    	
    	startService(intent);
    }
    

    /**
     * Starts the service to add the video
     */
    protected void addVideo() {

        Intent intent = new Intent(this, AddServerVideoService.class);
        intent.setAction(AndroidRestClientActivity.ACTION_ADDVIDEO);
    	
        intent.putExtra("outputPath", getIntent().getStringExtra("outputPath"));
        intent.putExtra("user", getIntent().getSerializableExtra("user"));
        
        startService(intent);
    }
    
    
    /**
     * When the video is uploaded
     * @param video
     */
    public void processServerVideo(Video video) {
    	this.video = video;
    	
    	// Send the PutVideo petition if the description was set
    	if (submitted == true) {
    		putVideo();
    	}
    }
    
    
    public void processShare() {
    	Log.e(AndroidRestClientActivity.APP_NAME, "processShare - END");
    }
    
    /**
     * Outputs info about a problem, used to notice system failures
     * @param message
     */
    public void showProblem(String message) {

    	Log.e(AndroidRestClientActivity.APP_NAME, message);
    	Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
