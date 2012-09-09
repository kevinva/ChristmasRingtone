package com.kevin.ringtone50.media;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.kevin.ringtone50.activity.MainActivity;

public class RingtonePlayer {
	
	private MainActivity mainRef;
	private MediaPlayer mp;

	private String ringtoneFile;  	//播放的文件	
	
	public RingtonePlayer(MainActivity ref){
		this.mainRef = ref;
	}	
	
	public void play(){
		AssetFileDescriptor assetFd;
		if(this.ringtoneFile != null){
			try {
				mp = new MediaPlayer();
				assetFd = mainRef.getAssets().openFd(this.ringtoneFile);
				mp.setDataSource(assetFd.getFileDescriptor(), assetFd.getStartOffset(), assetFd.getLength());
				mp.prepare();
				mp.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void stop(){
		if(mp != null){
			mp.stop();
			mp.release();
			mp = null;
			this.ringtoneFile = null;
		}
	}
	
	public boolean isPlaying(){
		if(mp != null){
			return mp.isPlaying();
		}
		return false;
	}
	
	public void setFiletoPlay(String file){
		this.ringtoneFile = file;
	}

}
