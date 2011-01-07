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

import java.io.IOException;
import android.os.Bundle;
import android.app.Activity;
import android.media.MediaPlayer;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AudioPlayblack extends Activity {
	private String music_path;
	private String music_name;
	private TextView label;
	private Button play_button;
	private Button close_button;
	private MediaPlayer mp;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
							 WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		setContentView(R.layout.audio_layout);
		mp = new MediaPlayer();

		label = (TextView)findViewById(R.id.music_label);
		play_button = (Button)findViewById(R.id.media_play_button);
		play_button.setText("Preview");
		play_button.setOnClickListener(new ButtonHandler());
		
		close_button = (Button)findViewById(R.id.media_close_button);
		close_button.setText("Close");
		close_button.setOnClickListener(new ButtonHandler());
		
		music_path = getIntent().getExtras().getString("MUSIC PATH");
		music_name = music_path.substring(music_path.lastIndexOf("/") + 1, music_path.length());
		
		label.setText("Audio file: " + music_name);
	}
	
	private void close() {
		finish();
	}
	
	private class ButtonHandler implements OnClickListener {
		private boolean init = false;
		
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.media_play_button) {
				if(!init) {
					try {
						mp.setDataSource(music_path);
						mp.prepare();
						mp.start();
						init = true;
					} catch (IOException e) {
						e.printStackTrace();	
					}
					
				} else {
					if(mp.isPlaying())
						mp.pause();
					else
						mp.start();
				}
				
			} else if(v.getId() == R.id.media_close_button)  {
				if(mp.isPlaying())
					mp.stop();
				mp.release();
				close();
			}
		}
	}
}
