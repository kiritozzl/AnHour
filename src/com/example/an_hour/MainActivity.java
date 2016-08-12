package com.example.an_hour;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private TextView tv;
	private Intent intent;
	private boolean isTime = false;
	private String time;
	
	private long pattern [] = {0,1000,1000,1000,1000,1000,1000};
	private Vibrator vibrator;
	private Notification mBuilder;
	private Switch sw_noficate;
	private Intent i_notifi;
	private PendingIntent pi;
	private NotificationManager nm;
	private boolean isNotify = false;
	
	private Switch sw_clock;
	private AlarmManager am;
	private Intent alarmIntent;
	private PendingIntent alarm_pi;
	private MediaPlayer mp;
	private boolean isClock = false;
	
	private Switch sw_vibrate;
	private boolean isVibrate = false;
	
	private LinearLayout layout;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tv = (TextView) findViewById(R.id.tv);
        vibrator = (Vibrator) MyApplication.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        sw_noficate = (Switch) findViewById(R.id.notificate_sw);
        
        intent = new Intent(this,AlarmService.class);
        i_notifi = new Intent(this,MainActivity.class);
        pi = PendingIntent.getActivity(this, 0, i_notifi, PendingIntent.FLAG_UPDATE_CURRENT);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        setNotificate();
        
        sw_clock = (Switch) findViewById(R.id.clock_sw);
        setClock();
        
        sw_vibrate = (Switch) findViewById(R.id.vibrate_sw);
        setVibrate();
        
        layout = (LinearLayout) findViewById(R.id.layout);
        
        startService(intent);
        registerReceiver(infoReceiver, new IntentFilter(AlarmService.BROADCAST_ACTION));
    }

	private void setVibrate() {
		sw_vibrate.setTextOff("打开震动提醒");
		sw_vibrate.setTextOn("关闭震动提醒");
		sw_vibrate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isVibrate = isChecked;
			}
		});
	}

	private void setClock() {
		sw_clock.setTextOff("打开闹钟通知");
		sw_clock.setTextOn("关闭闹钟通知");
		sw_clock.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isClock = isChecked;
				if (isChecked) {
					am = (AlarmManager) getSystemService(ALARM_SERVICE);
					alarmIntent = new Intent(MainActivity.this,ClockReceiver.class);
					alarm_pi = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
				}
			}
		});
	}
	
	private void setNotificate() {
		sw_noficate.setTextOff("打开状态栏通知");
        sw_noficate.setTextOn("关闭状态栏通知");
        sw_noficate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isNotify = isChecked;
				if (isChecked) {	
					mBuilder = new Notification.Builder(MainActivity.this)
					.setSmallIcon(R.drawable.a)
					.setContentTitle("正点时间到了！")
					.setContentText("现在时间是:" + time)
					.setContentIntent(pi)
					.setAutoCancel(true).build();
				}
			}
        });
	}
    
    private BroadcastReceiver infoReceiver = new BroadcastReceiver(){
    	public void onReceive(Context context, Intent intent) {
    		receiverInfo(intent);
    	}
    };
    
	@Override
	public void onResume() {
		super.onResume();
    	startService(intent);
    	registerReceiver(infoReceiver, new IntentFilter(AlarmService.BROADCAST_ACTION));
    };

    /*@Override
    protected void onDestroy() {
    	super.onDestroy();
    	unregisterReceiver(infoReceiver);
    	stopService(intent);
    }*/

    /**
     * 不断接收来自AlarmService的消息
     * @param i
     */
	private void receiverInfo(Intent i) {
		isTime = i.getBooleanExtra("now", false);
		time = i.getStringExtra("time");
		tv.setText(time);
		if (isTime) {
			if (isVibrate) {
				vibrator.vibrate(pattern, -1);
			}
			Toast.makeText(this, "now is :"+ time, Toast.LENGTH_LONG).show();
			//Log.e("Error", "Mainactivity isTime----"+isTime);
			if (isNotify) {
				nm.notify(001, mBuilder);
			}
			if (isClock) {
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(System.currentTimeMillis());
				am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarm_pi);
			}
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			startActivityForResult(intent, 0001);
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0001 && resultCode == Activity.RESULT_OK
				&& data != null) {
			Uri selectedImage = data.getData();
			String [] filePathColumn = {MediaStore.Images.Media.DATA};
			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null,
					null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String path = cursor.getString(columnIndex);
			
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			layout.setBackground(new BitmapDrawable(getResources(), bitmap));
		}
	}
}
