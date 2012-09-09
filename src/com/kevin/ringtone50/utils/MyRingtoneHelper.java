package com.kevin.ringtone50.utils;

import java.io.File;

import android.content.ContentValues;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;

import com.kevin.ringtone50.R;
import com.kevin.ringtone50.activity.MainActivity;

public class MyRingtoneHelper {
	
	private static MainActivity mainRef = null;
	private static MyRingtoneHelper instance = null;
	
	public MyRingtoneHelper(MainActivity ref){
		this.mainRef = ref;
	}
	
	//将铃声加入到默认铃声列表（但还未设置为默认铃声）
	public String prepareDefaultRingtone(String ringtonePath, int ringtoneType, String customizedName){
		String ringtone_prefix = mainRef.getString(R.string.app_name);
		
    	File f = new File(ringtonePath);
    	System.out.println("f path=" + f.getAbsolutePath());
    	ContentValues values = new ContentValues();
    	values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
    	values.put(MediaStore.MediaColumns.TITLE, ringtone_prefix + ": " + customizedName);
    	values.put(MediaStore.MediaColumns.SIZE, f.length());
    	//values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
    	//values.put(MediaStore.Audio.Media.IS_MUSIC, true);
    	switch(ringtoneType){
    	case RingtoneManager.TYPE_RINGTONE:
    		values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
    		break;
    	case RingtoneManager.TYPE_NOTIFICATION:
    		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
    		break;
    	case RingtoneManager.TYPE_ALARM:
    		values.put(MediaStore.Audio.Media.IS_ALARM, true);
    		break;
    		
    	}    	   	
    	Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
    	Uri newUri = mainRef.getContentResolver().insert(uri, values);       	
    	System.out.println("uri=" + newUri);    	
    	return newUri.toString();
	}
	
	//设置为默认铃声
	public void setDefaultRingtone(int ringtoneType, String ringtoneUriStr){
		Uri uri = Uri.parse(ringtoneUriStr);
		RingtoneManager.setActualDefaultRingtoneUri(mainRef, ringtoneType, uri); //设置铃声关键语句
	}
	
	//去掉默认铃声设置
	public void unSetDefaultRingtone(String ringtoneUri){
		System.out.println("delete: uri=" + ringtoneUri);
		if(ringtoneUri != null){
			int rowid = mainRef.getContentResolver().delete(Uri.parse(ringtoneUri), null, null);
			System.out.println("Delete row: " + rowid);
		}		
	}		

}
