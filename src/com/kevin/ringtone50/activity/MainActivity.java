package com.kevin.ringtone50.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.BIdNdKzg.AGTSwiNR38382.Airpush;
import com.adsmogo.adview.AdsMogoLayout;
import com.kevin.ringtone50.R;
import com.kevin.ringtone50.adapter.ButtonAdapter;
import com.kevin.ringtone50.data_model.MyRingtone;
import com.kevin.ringtone50.utils.Constants;
import com.kevin.ringtone50.utils.FileHandler;
import com.kevin.ringtone50.utils.MyRingtoneHelper;


public class MainActivity extends Activity {
	
	//private AdView adView;
	private GridView gridView;
	private ProgressDialog progressD;
	private ButtonAdapter adapter;
	
	private ArrayList<MyRingtone> ringtoneList;	
	
	private FileHandler fileHandler;
	private MyRingtoneHelper ringtoneHelper;
	
	private int currentClickedIndex = -1; //当前点击的按钮序号
	private int currentInstallRingtoneType = -111;
	
	private Handler copyFileHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(progressD != null && progressD.isShowing()){
				progressD.dismiss();
			}
			showMessageDidInstall();
		}		
		
	};
	
	private Airpush airpush;
	private AdsMogoLayout adsMogoLayoutCode;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate");        

        
        setContentView(R.layout.main);        
        
        this.addAdMogoLayout();
        this.addAirpush();
        
        this.fileHandler = new FileHandler(this);
        this.ringtoneHelper = new MyRingtoneHelper(this);
        
        this.prepareRingtone();
        this.initLayout();    
        
        
    }
    
    public void onStart(){
    	super.onStart();    	
    	System.out.println("onStart");
    	boolean isFirstRun = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.FIRST_RUN_PREFERENCE, true);
    	//System.out.println("isFirstRun: " + isFirstRun);
    	if(isFirstRun){
    		this.showInstruction();
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    		prefs.edit().putBoolean(Constants.FIRST_RUN_PREFERENCE, false).commit();
    	}
    }
    
    public void onPause(){
    	super.onPause();
    	System.out.println("onPause!");
    	this.adapter.getRingtonePlayer().stop();
    	this.saveRingtoneSetting();
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	prefs.edit().putBoolean(Constants.SETTING_FILE_EXIST, true).commit();
    }    
    
    public void onDestroy(){   
    	super.onDestroy();
    	System.out.println("onDestroy");

    	if(this.progressD != null && this.progressD.isShowing()){
    		this.progressD.dismiss();
    	}
    	
		if (adsMogoLayoutCode != null) {
			adsMogoLayoutCode.clearThread();
		}		
    	
    }
 
    @Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
    	super.onContextItemSelected(item);    	
    	
    	MyRingtone ringtone = this.ringtoneList.get(currentClickedIndex);
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	switch(item.getItemId()){
    	case Constants.MENU_INSTALL_RINGTONE:
    		this.currentInstallRingtoneType = RingtoneManager.TYPE_RINGTONE;
    		installRingtone(Constants.DIR_RINGTONE);
    		break;
    	case Constants.MENU_INSTALL_NOTIFICATION:
    		this.currentInstallRingtoneType = RingtoneManager.TYPE_NOTIFICATION;
    		this.installRingtone(Constants.DIR_NOTIFICATION);
    		break;
    	case Constants.MENU_INSTALL_ALARM:   
    		this.currentInstallRingtoneType = RingtoneManager.TYPE_ALARM;
    		this.installRingtone(Constants.DIR_ALARM);
    		break;
    	case Constants.MENU_MANAGE_RINGTONE:
    		this.currentInstallRingtoneType = RingtoneManager.TYPE_RINGTONE;
    		break;
    	case Constants.MENU_MANAGE_NOTIFICATION:
    		this.currentInstallRingtoneType = RingtoneManager.TYPE_NOTIFICATION;
    		break;
    	case Constants.MENU_MANAGE_ALARM:
    		this.currentInstallRingtoneType = RingtoneManager.TYPE_ALARM;
    		break;    		
    	case Constants.MENU_UNINSTALL_RINGTONE:
    		this.uninstallRingtone(Constants.DIR_RINGTONE);
    		Toast.makeText(this, R.string.toast_uninstall_ringtone, 1000).show();
    		break;
    	case Constants.MENU_UNINSTALL_NOTIFICATION:
    		this.uninstallRingtone(Constants.DIR_NOTIFICATION);
    		Toast.makeText(this, R.string.toast_uninstall_notification, 1000).show();
    		break;
    	case Constants.MENU_UNINSTALL_ALARM:
    		this.uninstallRingtone(Constants.DIR_ALARM);
    		Toast.makeText(this, R.string.toast_uninstall_alarm, 1000).show();
    		break;
    	case Constants.MENU_DEFAULT_RINGTONE:    		
    		boolean ringtoneHasSet = prefs.getString(Constants.CURRENT_RINGTONE, Constants.DEFAULT_RINGTONE_FILENAME).equalsIgnoreCase(ringtone.getRingtoneFilename());
    		if(!ringtoneHasSet){
    			this.setDefaultRingtone(RingtoneManager.TYPE_RINGTONE);
    		}else{
    			Toast.makeText(this, R.string.toast_has_set_defalut_ringtone, 1000).show();
    		}    		
    		break;
    	case Constants.MENU_DEFAULT_NOTIFICATION:
    		boolean notificationHasSet = prefs.getString(Constants.CURRENT_NOTIFICATION, Constants.DEFAULT_RINGTONE_FILENAME).equalsIgnoreCase(ringtone.getRingtoneFilename());
    		if(!notificationHasSet){
    			this.setDefaultRingtone(RingtoneManager.TYPE_NOTIFICATION);
    		}else{
    			Toast.makeText(this, R.string.toast_has_set_defalut_notification, 1000).show();
    		}    		
    		break;
    	case Constants.MENU_DEFAULT_ALARM:
    		boolean alarmHasSet = prefs.getString(Constants.CURRENT_ALARM, Constants.DEFAULT_RINGTONE_FILENAME).equalsIgnoreCase(ringtone.getRingtoneFilename());    		
    		if(!alarmHasSet){
    			this.setDefaultRingtone(RingtoneManager.TYPE_ALARM);
    			
    		}else{
    			Toast.makeText(this, R.string.toast_has_set_defalut_alarm, 1000).show();
    		}    		
    		break;
    	case Constants.MENU_CANCEL:
    		//do nothing
    	}

		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		System.out.println("menu = " + menu + "\nview = " + v + "\nmenuInfo = " + menuInfo);
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.clear();
		
		Button btn = (Button)v;
		String btnTitle = btn.getText().toString();
		menu.setHeaderTitle(btnTitle);		
		
		//提取按钮序号		
		this.currentClickedIndex = (Integer)btn.getTag();
		System.out.println("btn clicked index: " + this.currentClickedIndex);
		
		//MyRingtone ringtone = this.ringtoneList.get(currentClickedIndex);
		//System.out.println("ringtone: " + ringtone.isSetRingtone() + ", notification: " + ringtone.isSetNotification() + ", alarm: " + ringtone.isSetAlarm());
		
		if(!this.ringtoneList.get(currentClickedIndex).isInstallRingtone()){
			//System.out.println("install ringtone!");
			menu.add(0, Constants.MENU_INSTALL_RINGTONE, 0, R.string.ctx_menu_install_ringtone);
		}else{
			//System.out.println("manage ringtone!");
			menu.setHeaderTitle(btnTitle);
			SubMenu subMenuManageRingtone = menu.addSubMenu(0, Constants.MENU_MANAGE_RINGTONE, 0, R.string.ctx_menu_manage_ringtone);
			subMenuManageRingtone.add(0, Constants.MENU_UNINSTALL_RINGTONE, 0, R.string.ctx_menu_uninstall_ringtone);
			subMenuManageRingtone.add(0, Constants.MENU_DEFAULT_RINGTONE, 0, R.string.ctx_menu_default_ringtone);
			subMenuManageRingtone.add(0, Constants.MENU_CANCEL, 0, R.string.ctx_menu_cancel);
		}
		
		if(!this.ringtoneList.get(currentClickedIndex).isInstallNotification()){
			menu.add(0, Constants.MENU_INSTALL_NOTIFICATION, 0, R.string.ctx_menu_install_notification);
		}else{
			SubMenu subMenuManageNotification = menu.addSubMenu(0, Constants.MENU_MANAGE_NOTIFICATION, 0, R.string.ctx_menu_manage_notification);
			subMenuManageNotification.add(0, Constants.MENU_UNINSTALL_NOTIFICATION, 0, R.string.ctx_menu_uninstall_notification);
			subMenuManageNotification.add(0, Constants.MENU_DEFAULT_NOTIFICATION, 0, R.string.ctx_menu_default_notification);
			subMenuManageNotification.add(0, Constants.MENU_CANCEL, 0, R.string.ctx_menu_cancel);
		}
		
		if(!this.ringtoneList.get(currentClickedIndex).isInstallAlarm()){
			menu.add(0, Constants.MENU_INSTALL_ALARM, 0, R.string.ctx_menu_install_alarm);
		}else{
			SubMenu subMenuManageAlarm = menu.addSubMenu(0, Constants.MENU_MANAGE_ALARM, 0, R.string.ctx_menu_manage_alarm);
			subMenuManageAlarm.add(0, Constants.MENU_UNINSTALL_ALARM, 0, R.string.ctx_menu_uninstall_alarm);
			subMenuManageAlarm.add(0, Constants.MENU_DEFAULT_ALARM, 0, R.string.ctx_menu_default_alarm);
			subMenuManageAlarm.add(0, Constants.MENU_CANCEL, 0, R.string.ctx_menu_cancel);
		}
		
		menu.add(0, Constants.MENU_CANCEL, 0, R.string.ctx_menu_cancel);
		
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = this.getMenuInflater();
		menuInflater.inflate(R.menu.app_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		switch(item.getItemId()){
		case R.id.menu_instruction:
			this.showInstruction();
			break;
		case R.id.menu_uninstall_all:
			this.uninstallAllRingtones();
			Toast.makeText(this, R.string.toast_all_uninstall_successfully, 1000).show();
			break;
		case R.id.menu_more:
			String moreLink = this.getString(R.string.option_menu_more_link);
			this.openHTTPLink(moreLink);
			break;
		case R.id.menu_rate:
			String rateLink = this.getString(R.string.option_menu_rate_link);
			this.openHTTPLink(rateLink);
			break;
		case R.id.menu_quit:
			this.finish();
			break;
		case R.id.menu_share:
			this.shareViaUtils();			
			break;			
		default:
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {			
			//use smart wall on app exit. 
			airpush.startSmartWallAd();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initLayout(){
		//LinearLayout layout = (LinearLayout)findViewById(R.id.main_layout);
		//System.out.println("child count: " + layout.getChildCount() + ", index0: " + layout.getChildAt(0) + ", index1: " + layout.getChildAt(1));
		
        gridView = (GridView)findViewById(R.id.main_view);
        if(this.ringtoneList != null){
        	this.adapter = new ButtonAdapter(this, this.ringtoneList);
        	gridView.setAdapter(adapter);
        	
        }        
    }
	
    
    //显示操作指引（操作介绍）
    private void showInstruction(){
    	new AlertDialog.Builder(this)
    	.setTitle(R.string.dialog_title)
    	.setIcon(R.drawable.icon)
    	.setMessage(R.string.dialog_message)
    	.setNeutralButton(R.string.dialog_button_confirm, new DialogInterface.OnClickListener() {
			
		
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		})
		.show();
    }
    
    //加载铃声设置数据
    private void prepareRingtone(){
    	boolean setting_file_exist = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.SETTING_FILE_EXIST, false);
    	if(!setting_file_exist){
    		System.out.println("file not exists!");
    		
    		AssetManager assets = this.getAssets();
    		this.ringtoneList = new ArrayList<MyRingtone>();   
    		ArrayList<String> tempList = new ArrayList<String>();
        	try {
    			String[] files = assets.list("");
    			String[] names = this.getResources().getStringArray(R.array.ringtone_sys_name_list);
 
    			//过滤出铃声文件
    			for(String file: files){
    				String[] temp = file.split("\\.");
    				if(temp[temp.length - 1].equalsIgnoreCase("mp3") || 
    				   temp[temp.length - 1].equalsIgnoreCase("wav") ||
    				   temp[temp.length - 1].equalsIgnoreCase("wma")){
    					tempList.add(file);    					
    				}
    			}
    			
    			if(tempList.size() == names.length){
        			for(int i = 0; i < names.length; i++){    					
       					MyRingtone ringtone = new MyRingtone(tempList.get(i), names[i]);
    					ringtoneList.add(ringtone);
        						
        			}
    			}
    			
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    	}else{
    		System.out.println("file exists!");
    		
    		this.loadRingtoneSetting();
    	}
    	
		System.out.println("ringtoneList size = " + this.ringtoneList.size());
    }
    
    //从文件加载铃声设置数据
    private void loadRingtoneSetting(){
    	this.ringtoneList = fileHandler.loadDataListFromFile(Constants.FILE_RINGTONE_SETTING);
    	String[] names = this.getResources().getStringArray(R.array.ringtone_sys_name_list);
    	for(int i = 0; i < names.length; i++){
    		if(!this.ringtoneList.get(i).getCustomizedName().equals(names[i])){
    			this.ringtoneList.get(i).setCustomizedName(names[i]);
    		}
    	}
    	
    }
    
   //保存铃声设置数据
    private void saveRingtoneSetting(){
    	this.fileHandler.saveDataListToFile(this.ringtoneList, Constants.FILE_RINGTONE_SETTING);
    }
    
    //将铃声复制到sdcard指定目录以安装
    private void installRingtone(String dest){
    	
    	//1.设置标志位
    	final MyRingtone ringtone = this.ringtoneList.get(currentClickedIndex);
    	if(dest.equals(Constants.DIR_RINGTONE)){
    		ringtone.setInstallRingtone(true);
    	}else if(dest.equals(Constants.DIR_NOTIFICATION)){
    		ringtone.setInstallNotification(true);
    	}else if(dest.equals(Constants.DIR_ALARM)){
    		ringtone.setInstallAlarm(true);
    	}
    	
    	//2.将铃声文件复制到sdcard指定目录
    	boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    	if(sdCardExist){
    		final String destPath = Environment.getExternalStorageDirectory().toString() + dest;
    		File f = new File(destPath);
    		if(!f.exists()){
    			f.mkdirs();
    		} 
    		
    		String title = this.getString(R.string.progress_dialog_title_wait);
    		String message = this.getString(R.string.progress_dialog_message_installing);
    		this.progressD = ProgressDialog.show(this, title, message, false, false);
    		
    		new Thread(new Runnable(){
    			
				
				public void run() {
					// TODO Auto-generated method stub
					fileHandler.copyFileToDest(ringtone.getRingtoneFilename(), destPath);
					
					//3.将铃声加入到系统铃声列表中
					MyRingtone ringtone = MainActivity.this.ringtoneList.get(currentClickedIndex);					
					String sdcardDir = Environment.getExternalStorageDirectory().toString();
					String uriStr = "";
					switch(currentInstallRingtoneType){
					case RingtoneManager.TYPE_RINGTONE:
						uriStr = ringtoneHelper.prepareDefaultRingtone(sdcardDir + Constants.DIR_RINGTONE + ringtone.getRingtoneFilename(), currentInstallRingtoneType, ringtone.getCustomizedName());
						ringtone.setRingtoneUri(uriStr);
						break;
					case RingtoneManager.TYPE_NOTIFICATION:
						uriStr = ringtoneHelper.prepareDefaultRingtone(sdcardDir + Constants.DIR_NOTIFICATION + ringtone.getRingtoneFilename(), currentInstallRingtoneType, ringtone.getCustomizedName());
						ringtone.setNotificationUri(uriStr);
						break;
					case RingtoneManager.TYPE_ALARM:
						uriStr = ringtoneHelper.prepareDefaultRingtone(sdcardDir + Constants.DIR_ALARM + ringtone.getRingtoneFilename(), currentInstallRingtoneType, ringtone.getCustomizedName());
						ringtone.setAlarmUri(uriStr);
						break;
					}
					
					copyFileHandler.sendEmptyMessage(0);					
				}    			
    			
    		}).start(); 		
    		
    	}   

    }
    
    
    //将铃声文件从指定目录删除
    private void uninstallRingtone(String dest){
    	
    	//1.设置标志位
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	MyRingtone ringtone = this.ringtoneList.get(currentClickedIndex);
    	String ringtoneUri = null;
    	if(dest.equals(Constants.DIR_RINGTONE)){
    		if(prefs.getString(Constants.CURRENT_RINGTONE, Constants.DEFAULT_RINGTONE_FILENAME).equalsIgnoreCase(ringtone.getRingtoneFilename())){
    			prefs.edit().putString(Constants.CURRENT_RINGTONE, Constants.DEFAULT_RINGTONE_FILENAME).commit();
    		}
    		ringtone.setInstallRingtone(false);
    		ringtoneUri = ringtone.getRingtoneUri();
    		ringtone.setRingtoneUri(null);
    	}else if(dest.equals(Constants.DIR_NOTIFICATION)){ 
    		if(prefs.getString(Constants.CURRENT_NOTIFICATION, Constants.DEFAULT_RINGTONE_FILENAME).equalsIgnoreCase(ringtone.getRingtoneFilename())){
    			prefs.edit().putString(Constants.CURRENT_NOTIFICATION, Constants.DEFAULT_RINGTONE_FILENAME).commit();
    		}
    		ringtone.setInstallNotification(false);
    		ringtoneUri = ringtone.getNotificationUri();
    		ringtone.setNotificationUri(null);
    	}else if(dest.equals(Constants.DIR_ALARM)){
    		if(prefs.getString(Constants.CURRENT_ALARM, Constants.DEFAULT_RINGTONE_FILENAME).equalsIgnoreCase(ringtone.getRingtoneFilename())){
    			prefs.edit().putString(Constants.CURRENT_ALARM, Constants.DEFAULT_RINGTONE_FILENAME).commit();
    		}
    		ringtone.setInstallAlarm(false);
    		ringtoneUri = ringtone.getAlarmUri();
    		ringtone.setAlarmUri(null);
    	}
    	
    	//2.将铃声文件从sdcard指定目录删除
    	boolean sdcardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    	if(sdcardExist){
    		String sdcardDir = Environment.getExternalStorageDirectory().toString();
    		boolean deleteSuccessfully = this.fileHandler.removeFileFromDest(sdcardDir + dest, ringtone.getRingtoneFilename());
    		System.out.println("delete ringtone successfully? " + deleteSuccessfully);
    		if(deleteSuccessfully){
    			//3.从手机系统列表中删除当前铃声
    			ringtoneHelper.unSetDefaultRingtone(ringtoneUri);
    		}
    	}        	
    	
    }      
    
    private void showMessageDidInstall(){
    	Toast.makeText(this, R.string.toast_install_succeed, 1000).show();
    	int msgId = -123;    	
    	switch(this.currentInstallRingtoneType){
    	case RingtoneManager.TYPE_RINGTONE:
    		msgId = R.string.dialog_whether_set_default_ringtone;
    		break;
    	case RingtoneManager.TYPE_NOTIFICATION:
    		msgId = R.string.dialog_whether_set_default_notification;
    		break;
    	case RingtoneManager.TYPE_ALARM:
    		msgId = R.string.dialog_whether_set_default_alarm;
    		break;
    	}
    	
    	new AlertDialog.Builder(this)
    	.setMessage(msgId)
    	.setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
			
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//设为当前默认铃声
				MainActivity.this.setDefaultRingtone(currentInstallRingtoneType);
			}
		})
		.setNegativeButton(R.string.dialog_button_no, new DialogInterface.OnClickListener() {
			
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		})
		.show();
    }
    
    //设置默认铃声
    private void setDefaultRingtone(int ringtoneType){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this); 
    	
		MyRingtone ringtone = MainActivity.this.ringtoneList.get(currentClickedIndex);
		int toastId = R.string.toast_set_default_ringtone;
		switch(ringtoneType){
		case RingtoneManager.TYPE_ALARM:
			toastId = R.string.toast_set_default_alarm;
			ringtoneHelper.setDefaultRingtone(ringtoneType, ringtone.getAlarmUri());
			prefs.edit().putString(Constants.CURRENT_ALARM, ringtone.getRingtoneFilename()).commit();
			break;
		case RingtoneManager.TYPE_NOTIFICATION:
			toastId = R.string.toast_set_default_notification;
			ringtoneHelper.setDefaultRingtone(ringtoneType, ringtone.getNotificationUri());
			prefs.edit().putString(Constants.CURRENT_NOTIFICATION, ringtone.getRingtoneFilename()).commit();
			break;
		case RingtoneManager.TYPE_RINGTONE:
			toastId = R.string.toast_set_default_ringtone;
			ringtoneHelper.setDefaultRingtone(ringtoneType, ringtone.getRingtoneUri());
			prefs.edit().putString(Constants.CURRENT_RINGTONE, ringtone.getRingtoneFilename()).commit();
			break;
		}
		
		Toast.makeText(this, toastId, 1000).show();
    }
    
    private void openHTTPLink(String urlPath){
    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlPath));
    	//intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
    	this.startActivity(intent);
    	
    }
    
	//加载上部AdMogo广告
	private void addAdMogoLayout(){
		adsMogoLayoutCode = new AdsMogoLayout(this,	this.getString(R.string.AdMogo_USER_ID2), false);		
		LinearLayout mainLayout = (LinearLayout)findViewById(R.id.main_layout);		
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    	mainLayout.addView(adsMogoLayoutCode, 0, params);
	}
	
	private void addAirpush(){
		// create Airpush constructor.
		airpush = new Airpush(this);
		airpush.startSmartWallAd(); //launch smart wall on App start
		airpush.startPushNotification(false);
		airpush.startIconAd();	
		/*
		 * Smart Wall ads: 1: Dialog Ad 2: AppWall Ad 3: LandingPage Ad Only one
		 * of the ad will get served at a time. SDK will ignore the other
		 * requests. To use them all give a gap of 20 seconds between calls.
		 */
		// start Dialog Ad
		 //airpush.startDialogAd();
		// start AppWall ad
		 airpush.startAppWall();
		// start Landing Page
		// airpush.startLandingPageAd();	

	}
    
    //卸载所有铃声
    private void uninstallAllRingtones(){
    	String sdcardDir = Environment.getExternalStorageDirectory().toString();
    	for(MyRingtone ringtone: this.ringtoneList){
    		if(ringtone.isInstallAlarm()){
    			fileHandler.removeFileFromDest(sdcardDir + Constants.DIR_ALARM, ringtone.getRingtoneFilename());
    			ringtoneHelper.unSetDefaultRingtone(ringtone.getAlarmUri());
    			ringtone.setInstallAlarm(false);
    		}
    		if(ringtone.isInstallNotification()){
    			fileHandler.removeFileFromDest(sdcardDir + Constants.DIR_NOTIFICATION, ringtone.getRingtoneFilename());
    			ringtoneHelper.unSetDefaultRingtone(ringtone.getNotificationUri());
    			ringtone.setInstallNotification(false);
    		}
    		if(ringtone.isInstallRingtone()){
    			fileHandler.removeFileFromDest(sdcardDir + Constants.DIR_RINGTONE, ringtone.getRingtoneFilename());
    			ringtoneHelper.unSetDefaultRingtone(ringtone.getRingtoneUri());
    			ringtone.setInstallRingtone(false);
    		}
    	}
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	Editor prefsEditor = prefs.edit();
    	prefsEditor.putString(Constants.CURRENT_ALARM, Constants.DEFAULT_RINGTONE_FILENAME);
    	prefsEditor.putString(Constants.CURRENT_NOTIFICATION, Constants.DEFAULT_RINGTONE_FILENAME);
    	prefsEditor.putString(Constants.CURRENT_RINGTONE, Constants.DEFAULT_RINGTONE_FILENAME);
    	prefsEditor.commit();
    }
    
    private void shareViaUtils(){
    	String title = this.getString(R.string.share_title);
    	String text_prefix = this.getString(R.string.share_text_prefix);
    	String text_content = this.getString(R.string.share_text_content);
    	
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, text_prefix + text_content);
		startActivity(Intent.createChooser(intent, this.getTitle()));
    }

}