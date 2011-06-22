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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.CompoundButton;

public class Settings extends Activity {
	private boolean mHiddenChanged = false;
	private boolean mColorChanged = false;
	private boolean mThumbnailChanged = false;
	private boolean mSortChanged = false;
	private boolean mSpaceChanged = false;
	
	private boolean hidden_state;
	private boolean thumbnail_state;
	private int color_state, sort_state, mSpaceState;
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
		mSpaceState = i.getExtras().getInt("SPACE");
				
		final CheckBox hidden_bx = (CheckBox)findViewById(R.id.setting_hidden_box);
		final CheckBox thumbnail_bx = (CheckBox)findViewById(R.id.setting_thumbnail_box);
		final CheckBox space_bx = (CheckBox)findViewById(R.id.setting_storage_box);
		final ImageButton color_bt = (ImageButton)findViewById(R.id.setting_text_color_button);
		final ImageButton sort_bt = (ImageButton)findViewById(R.id.settings_sort_button);
		
		hidden_bx.setChecked(hidden_state);
		thumbnail_bx.setChecked(thumbnail_state);
		space_bx.setChecked(mSpaceState == View.VISIBLE);
		
		color_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
				CharSequence[] options = {"White", "Magenta", "Yellow", "Red", "Cyan",
									      "Blue", "Green"};
				int index = ((color_state & 0x00ffffff) << 2) % options.length;
				
				builder.setTitle("Change text color");
				builder.setIcon(R.drawable.color);
				builder.setSingleChoiceItems(options, index, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int index) {
						switch(index) {
							case 0:
								color_state = Color.WHITE;
								is.putExtra("COLOR", color_state);
								mColorChanged = true;
								
								break;
							case 1:
								color_state = Color.MAGENTA;
								is.putExtra("COLOR", color_state);
								mColorChanged = true;
								
								break;
							case 2:
								color_state = Color.YELLOW;
								is.putExtra("COLOR", color_state);
								mColorChanged = true;
								
								break;
							case 3:
								color_state = Color.RED;
								is.putExtra("COLOR", color_state);
								mColorChanged = true;
								
								break;
							case 4:
								color_state = Color.CYAN;
								is.putExtra("COLOR", color_state);
								mColorChanged = true;
								
								break;
							case 5:
								color_state = Color.BLUE;
								is.putExtra("COLOR", color_state);
								mColorChanged = true;
								
								break;
							case 6:
								color_state = Color.GREEN;
								is.putExtra("COLOR", color_state);
								mColorChanged = true;
								
								break;
						}
					}
				});
				
				builder.create().show();
			}
		});
		
		hidden_bx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				hidden_state = isChecked;
				
				is.putExtra("HIDDEN", hidden_state);
				mHiddenChanged = true;
			}
		});
		
		thumbnail_bx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				thumbnail_state = isChecked;
				
				is.putExtra("THUMBNAIL", thumbnail_state);
				mThumbnailChanged = true;
			}
		});
		
		space_bx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) 
					mSpaceState = View.VISIBLE;
				else 
					mSpaceState = View.GONE;
				
				mSpaceChanged = true;
				is.putExtra("SPACE", mSpaceState);				
			}
		});
		
		sort_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
    			CharSequence[] options = {"None", "Alphabetical", "Type", "Size"};
    			
    			builder.setTitle("Sort by...");
    			builder.setIcon(R.drawable.filter);
    			builder.setSingleChoiceItems(options, sort_state, new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int index) {
						switch(index) {
						case 0:
							sort_state = 0;
							mSortChanged = true;
							is.putExtra("SORT", sort_state);
							break;
							
						case 1:
							sort_state = 1;
							mSortChanged = true;
							is.putExtra("SORT", sort_state);
							break;
							
						case 2:
							sort_state = 2;
							mSortChanged = true;
							is.putExtra("SORT", sort_state);
							break;
						
						case 3:
							sort_state = 3;
							mSortChanged = true;
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
		
		if(!mSpaceChanged)
			is.putExtra("SPACE", mSpaceState);
		
		if(!mHiddenChanged)
			is.putExtra("HIDDEN", hidden_state);
		
		if(!mColorChanged)
			is.putExtra("COLOR", color_state);
		
		if(!mThumbnailChanged)
			is.putExtra("THUMBNAIL", thumbnail_state);
		
		if(!mSortChanged)
			is.putExtra("SORT", sort_state);
			
		setResult(RESULT_CANCELED, is);
	}
}
