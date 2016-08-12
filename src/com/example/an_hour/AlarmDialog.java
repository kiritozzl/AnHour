package com.example.an_hour;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder.AudioSource;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;

public class AlarmDialog extends Activity {
	private MediaPlayer mp;
	private String time;
	private Calendar calendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_layout);
		playMusic();
		showAlarmDialog();
	}

			private void showAlarmDialog() {
				calendar = Calendar.getInstance();
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy,MM,dd,EEEE,HH:mm:ss");
		        time = sdf1.format(calendar.getTime());
				AlertDialog.Builder builder = new AlertDialog.Builder(this)
									.setMessage("now is:"+time)
									.setTitle("正点时间到了")
									.setCancelable(true)
									.setPositiveButton("OK", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											mp.stop();
											mp.release();
											dialog.dismiss();
											AlarmDialog.this.finish();
										}
									});
				AlertDialog dialog = builder.create();
				dialog.show();
				
			}
		    
			private void playMusic() {
				//mp = MediaPlayer.create(this, R.raw.a);
				Uri uri = Uri.parse("/storage/sdcard1/kgmusic/download/Claris - 魔法少女小圆.mp3");
				mp = new MediaPlayer();
				mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
				try {
					mp.setDataSource(AlarmDialog.this,uri);
					mp.prepare();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				mp.start();
			}

			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					Intent intent = new Intent(this,MainActivity.class);
					startActivity(intent);
				}
				return super.onKeyDown(keyCode, event);
			}
}
