package com.nexes.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.util.Log;


public class SettingsManager {
	private static final String FILE_NAME = "/sdcard/open manager/settings_manager.prfs";
	private final FileManager file_mg;
	private int hidden_state = 0;
	
	public SettingsManager(FileManager FileMan) {
		file_mg = FileMan;
		
		//read user settings before populating the directories files
		readSettings();
		
		//create a directory to hold our preference file if it doesn't already exist
		File dir = new File("/sdcard/open manager/");
		try {
			if(!dir.exists())
				if(dir.mkdir())
					new File(FILE_NAME).createNewFile();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int readSettings() {
		
		try {
			FileInputStream fis = new FileInputStream(FILE_NAME);
			if(fis != null) 
				hidden_state = fis.read();
			
			fis.close();
			
		} catch (FileNotFoundException e) {
			Log.e("EventHandler","FileNotFoundException");	
			
		} catch (IOException e) {
			Log.e("EventHandler","IOException");
		}
		
		file_mg.setShowHiddenFiles(hidden_state);
		
		return hidden_state;
	}
	
	/**
	 * 
	 * @param data
	 */
	public void writeSettings(String data) {
		hidden_state = Integer.parseInt(data);
		
		try {
			FileOutputStream fos = new FileOutputStream(new File("/sdcard/open manager/" +FILE_NAME));
			fos.write(hidden_state);
			fos.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		file_mg.setShowHiddenFiles(hidden_state);
	}
}
