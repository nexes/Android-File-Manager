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

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.util.Log;

public class Settings extends Activity {
	private static final String PREF_FILE = "/sdcard/open manager/settings_manager.prfs";
	private SettingsManager setting;
	
	private CheckBox hidden;
	private int hidden_state, check;
	private boolean state_changed = false;
	private Intent is = new Intent();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		Intent i = getIntent();
		check = i.getExtras().getInt("HIDDEN");
		
		hidden = (CheckBox)findViewById(R.id.hidden_box);
		if(check == 1)
			hidden.setChecked(true);
		else
			hidden.setChecked(false);
		
		hidden.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(hidden.isChecked())
					hidden_state = 1;
				else
					hidden_state = 0;
				
				is.putExtra("HIDDEN", hidden_state + "");
				state_changed = true;
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(!state_changed)
			is.putExtra("HIDDEN", check + "");
		
		setResult(RESULT_CANCELED, is);
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
			
		try {
			FileOutputStream fos = new FileOutputStream(PREF_FILE);
			if(state_changed)
				fos.write(hidden_state);
			else
				fos.write(check);
			
			fos.close();
			
		} catch (FileNotFoundException e) {
			Log.e("Settings class", "FileNotFoundException");
			
		} catch (IOException e) {
			Log.e("Settings class", "IOException");
		}
	}
}
