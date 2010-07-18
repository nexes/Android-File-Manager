/*
    Open Manager, an open source file manager for the Android system
    Copyright (C) 2009, 2010  Joe Berria <nexesdevelopment@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
			Log.e("OpenManager", "couldn't create settings file IOException");
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
