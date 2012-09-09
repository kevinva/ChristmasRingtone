package com.kevin.ringtone50.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;

import com.kevin.ringtone50.activity.MainActivity;
import com.kevin.ringtone50.data_model.MyRingtone;

public class FileHandler {

	private MainActivity mainRef;
	
	public FileHandler(MainActivity ref){
		this.mainRef = ref;
	}
	
	public void copyFileToDest(String fileName, String destDir){
		AssetManager asset = mainRef.getAssets();
		try {			
			System.out.println("destPath: " + destDir + fileName);
			
			FileOutputStream fos = new FileOutputStream(destDir + fileName);
			InputStream is = asset.open(fileName);
			BufferedInputStream bufferedIs = new BufferedInputStream(is);
			int b;
			while((b = bufferedIs.read()) != -1){
				fos.write(b);
			}
			fos.flush();
			is.close();
			bufferedIs.close();
			fos.close();
			fos = null;
			bufferedIs = null;
			is = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean removeFileFromDest(String destDir, String filename){
		File f = new File(destDir + filename);
		if(f.exists()){
			boolean resultFlag = f.delete();
			return resultFlag;
		}
		return false;
	}
	
	public void saveDataListToFile(ArrayList<MyRingtone> dataList, String destFile){
		try {
			FileOutputStream fos = mainRef.openFileOutput(destFile, Context.MODE_PRIVATE);
			ObjectOutputStream objOs = new ObjectOutputStream(fos);
			objOs.writeObject(dataList);
			fos.close();
			objOs.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<MyRingtone> loadDataListFromFile(String sourceFile){
		ArrayList<MyRingtone> resultList = null;
		try {
			FileInputStream fis = mainRef.openFileInput(sourceFile);
			ObjectInputStream objIs = new ObjectInputStream(fis);
			resultList = (ArrayList<MyRingtone>)objIs.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultList;
	}

}
