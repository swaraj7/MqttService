package com.qonect.protocols.mqtt;

import java.sql.Timestamp;
import java.util.Date;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.qonect.protocols.mqtt.MqttServiceDelegate.MessageHandler;
import com.qonect.protocols.mqtt.MqttServiceDelegate.MessageReceiver;
import com.qonect.protocols.mqtt.MqttServiceDelegate.StatusHandler;
import com.qonect.protocols.mqtt.MqttServiceDelegate.StatusReceiver;
import com.qonect.protocols.mqtt.service.MqttService;
import com.qonect.protocols.mqtt.service.MqttService.ConnectionStatus;

public class MqttTestActivity extends Activity implements MessageHandler, StatusHandler
{	
	private static final String TAG = "MqttTestActivity";
	
	private MessageReceiver msgReceiver;
	private StatusReceiver statusReceiver;
	
	private TextView timestampView, topicView, messageView, statusView;
	
	
	@Override  
	public void onCreate(Bundle savedInstanceState)   
	{  
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.main_test);
		
		Log.d(TAG, "onCreate");
		
		timestampView = (TextView)findViewById(R.id.timestampView);
		topicView = (TextView)findViewById(R.id.topicView);
		messageView = (TextView)findViewById(R.id.messageView);	
		statusView = (TextView)findViewById(R.id.statusView);
	}  
	
	@Override
	protected void onResume()
	{
		Log.d(TAG, "onResume");
		super.onResume();
		
		bindStatusReceiver();
		bindMessageReceiver();

		MqttServiceDelegate.startService(this);
	}

	@Override  
	protected void onPause()   
	{ 
		Log.d(TAG, "onPause");
		
		//MqttServiceDelegate.stopService(this);
		
		unbindMessageReceiver();
		unbindStatusReceiver();
		
	    super.onPause(); 
	}
	
	private void bindMessageReceiver(){
		msgReceiver = new MessageReceiver();
		msgReceiver.registerHandler(this);
		registerReceiver(msgReceiver, 
			new IntentFilter(MqttService.MQTT_MSG_RECEIVED_INTENT));
	}
	
	private void unbindMessageReceiver(){
		if(msgReceiver != null){
			msgReceiver.unregisterHandler(this);
			unregisterReceiver(msgReceiver);
			msgReceiver = null;
		}
	}
	
	private void bindStatusReceiver(){
		statusReceiver = new StatusReceiver();
		statusReceiver.registerHandler(this);
		registerReceiver(statusReceiver, 
			new IntentFilter(MqttService.MQTT_STATUS_INTENT));
	}
	
	private void unbindStatusReceiver(){
		if(statusReceiver != null){
			statusReceiver.unregisterHandler(this);
			unregisterReceiver(statusReceiver);
			statusReceiver = null;
		}
	}
	
	private String getCurrentTimestamp(){
		return new Timestamp(new Date().getTime()).toString();  
	}

	@Override
	public void handleMessage(String topic, byte[] payload) {
		String message = new String(payload);
		
		Log.d(TAG, "handleMessage: topic="+topic+", message="+message);
				
		if(timestampView != null)timestampView.setText("When: "+getCurrentTimestamp());
		if(topicView != null)topicView.setText("Topic: "+topic);
		if(messageView != null)messageView.setText("Message: "+message);
	}
	
	

	@Override
	public void handleStatus(ConnectionStatus status, String reason) {
		Log.d(TAG, "handleStatus: status="+status+", reason="+reason);
		if(statusView != null)statusView.setText("Status: "+status.toString()+" ("+reason+")");
	}
}
