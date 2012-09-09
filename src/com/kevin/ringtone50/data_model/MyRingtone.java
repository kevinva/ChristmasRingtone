package com.kevin.ringtone50.data_model;

import java.io.Serializable;

public class MyRingtone implements Serializable{
	
	private String customizedName;
	private String ringtoneFilename;
	private String ringtoneUri;  //该铃声在媒体数据库中的标识
	private String notificationUri;
	private String alarmUri;
	private boolean isInstallRingtone;
	private boolean isInstallNotification;
	private boolean isInstallAlarm;

	
	public MyRingtone(String file, String sysName){		
		this.ringtoneFilename = file;
		this.customizedName = sysName;
		this.isInstallAlarm = false;
		this.isInstallNotification = false;
		this.isInstallRingtone = false;	
	}


	public boolean isInstallRingtone() {
		return isInstallRingtone;
	}

	public void setInstallRingtone(boolean isInstallRingtone) {
		this.isInstallRingtone = isInstallRingtone;
	}



	public boolean isInstallNotification() {
		return isInstallNotification;
	}



	public void setInstallNotification(boolean isInstallNotification) {
		this.isInstallNotification = isInstallNotification;
	}



	public boolean isInstallAlarm() {
		return isInstallAlarm;
	}



	public void setInstallAlarm(boolean isInstallAlarm) {
		this.isInstallAlarm = isInstallAlarm;
	}


	public String getRingtoneFilename() {
		return ringtoneFilename;
	}


	public String getRingtoneUri() {
		return ringtoneUri;
	}


	public void setRingtoneUri(String ringtoneUri) {
		this.ringtoneUri = ringtoneUri;
	}


	public String getNotificationUri() {
		return notificationUri;
	}


	public void setNotificationUri(String notificationUri) {
		this.notificationUri = notificationUri;
	}


	public String getAlarmUri() {
		return alarmUri;
	}


	public void setAlarmUri(String alarmUri) {
		this.alarmUri = alarmUri;
	}
	
	public String getCustomizedName(){
		return this.customizedName;
	}	
	
	public void setCustomizedName(String name){
		this.customizedName = name;
	}

	
}