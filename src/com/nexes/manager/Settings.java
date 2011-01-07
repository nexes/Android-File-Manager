/*
    Open Manager, an open source file manager for the Android system
    Copyright (C) 2009, 2010, 2011  Joe Berria <nexesdevelopment@gmail.com>

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

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.CompoundButton;
//import android.util.Log;

public class Settings extends Activity {
//	private CheckBox hidden_bx;
//	private CheckBox thumbnail_bx;
//	private ImageButton color_bt;
	
	private boolean hidden_changed = false;
	private boolean color_changed = false;
	private boolean thumbnail_changed = false;
	private boolean sort_changed = false;
	
	private boolean hidden_state;
	private boolean thumbnail_state;
	private int color_state;
	private int sort_state;
	private Intent is = new Intent();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		Intent i = getIntent();
		hidden_state = i.getExtras().getBoolean("HIDDEN");
		thumbnail_state = i.getExtras().getBoolean("THUMBNAIL");
		color_state = i.getExtras().getInt("COLOR");
		sort_state = i.getExtras().getInt("SORT");
				
		final CheckBox hidden_bx = (CheckBox)findViewById(R.id.setting_hidden_box);
		final CheckBox thumbnail_bx = (CheckBox)findViewById(R.id.setting_thumbnail_box);
		final ImageButton color_bt = (ImageButton)findViewById(R.id.setting_color_button);
		final ImageButton sort_bt = (ImageButton)findViewById(R.id.settings_sort_button);

		hidden_bx.setChecked(hidden_state);
		thumbnail_bx.setChecked(thumbnail_state);
		
		color_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
				AlertDialog dialog;
				CharSequence[] options = {"White", "Green", "Red", "Blue", "Cyan",
									      "Yellow", "Magenta"};
				
				builder.setTitle("Change text color");
				builder.setIcon(R.drawable.color);
				builder.setItems(options, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int index) {
						switch(index) {
							case 0:
								color_state = Color.WHITE;
								Toast.makeText(Settings.this, "White selected", Toast.LENGTH_SHORT).show();
								break;
							case 1:
								color_state = Color.GREEN;
								Toast.makeText(Settings.this, "Green selected", Toast.LENGTH_SHORT).show();
								break;
							case 2:
								color_state = Color.RED;
								Toast.makeText(Settings.this, "Red selected", Toast.LENGTH_SHORT).show();
								break;
							case 3:
								color_state = Color.BLUE;
								Toast.makeText(Settings.this, "Blue selected", Toast.LENGTH_SHORT).show();
								break;
							case 4:
								color_state = Color.CYAN;
								Toast.makeText(Settings.this, "Cyan selected", Toast.LENGTH_SHORT).show();
								break;
							case 5:
								color_state = Color.YELLOW;
								Toast.makeText(Settings.this, "Yellow selected", Toast.LENGTH_SHORT).show();
								break;
							case 6:
								color_state = Color.MAGENTA;
								Toast.makeText(Settings.this, "Magenta selected", Toast.LENGTH_SHORT).show();
								break;
						}
						is.putExtra("COLOR", color_state);
						color_changed = true;
					}
				});
				
				dialog = builder.create();
				dialog.show();
			}
		});
		
		hidden_bx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				hidden_state = hidden_bx.isChecked();
				
				is.putExtra("HIDDEN", hidden_state);
				hidden_changed = true;
			}
		});
		
		thumbnail_bx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				thumbnail_state = thumbnail_bx.isChecked();
				
				is.putExtra("THUMBNAIL", thumbnail_state);
				thumbnail_changed = true;
			}
		});
		
		sort_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
    			CharSequence[] options = {"None", "Alphabetical", "Type"};
    			
    			builder.setTitle("Sort by...");
    			builder.setItems(options, new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int index) {
						switch(index) {
						case 0:
							sort_state = 0;
							sort_changed = true;
							is.putExtra("SORT", sort_state);
							break;
							
						case 1:
							sort_state = 1;
							sort_changed = true;
							is.putExtra("SORT", sort_state);
							break;
							
						case 2:
							sort_state = 2;
							sort_changed = true;
							is.putExtra("SORT", sort_state);
							break;
						}
					}
				});
    			
    			builder.create().show();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(!hidden_changed)
			is.putExtra("HIDDEN", hidden_state);
		
		if(!color_changed)
			is.putExtra("COLOR", color_state);
		
		if(!thumbnail_changed)
			is.putExtra("THUMBNAIL", thumbnail_state);
		
		if(!sort_changed)
			is.putExtra("SORT", sort_state);
			
		setResult(RESULT_CANCELED, is);
	}
}
