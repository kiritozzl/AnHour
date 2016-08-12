package com.example.an_hour;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

public class AlarmService extends Service {
	private Calendar c;
	private String alarm_time;
	public static final String BROADCAST_ACTION = "com.websmithing.broadcasttest.displayevent";
	public Handler handler = new Handler();
	Intent intent;
	private String time;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
	}
	
	private Runnable sendInfo = new Runnable() {
		
		@Override
		public void run() {
			c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
	        alarm_time = sdf.format(c.getTime());
	        
	        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy,MM,dd,EEEE,HH:mm:ss");
	        time = sdf1.format(c.getTime());
			
			updateTime(time,alarm_time);
			handler.postDelayed(this, 0);
		}

	};

	private void updateTime(String time,String alarmTime) {
		intent.putExtra("time", time);
		intent.putExtra("now", isTime(alarmTime));
		//Log.e("Error", "alarm_time---"+alarmTime);
		//Log.e("Error", "alarmservice isTime(alarm_time)---"+isTime(alarmTime));
		sendBroadcast(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		handler.removeCallbacks(sendInfo);
		handler.postDelayed(sendInfo, 0);
		return super.onStartCommand(intent, flags, startId);
	}
	
	private boolean isTime(String t){
    	if (	t.equals("070000") || t.equals("080000") || t.equals("090000") ||
    			t.equals("100000") || t.equals("110000") || t.equals("120000") || 
    			t.equals("130000") || t.equals("140000") || t.equals("150000") || 
    			t.equals("160000") || t.equals("170000") || t.equals("180000") || 
    			t.equals("190000") || t.equals("200000") || t.equals("210000") || 
    			t.equals("220000") || t.equals("230000") || t.equals("240000")) {
			return true;
		}
    	else {
			return false;
		}
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(sendInfo);
	}
}
