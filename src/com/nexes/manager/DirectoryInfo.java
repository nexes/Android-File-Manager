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

import java.io.File;
import java.util.Date;
import android.os.Bundle;
import android.os.AsyncTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class DirectoryInfo extends Activity {
	private final int KB = 1024;
	private final int MG = KB * KB;
	private final int GB = MG * KB;
	private String path_name;
	private TextView name_label, path_label, dir_label,
					 file_label, time_label, total_label;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_layout);
		path_name = getIntent().getExtras().getString("PATH_NAME");
		
		name_label = (TextView)findViewById(R.id.name_label);
		path_label = (TextView)findViewById(R.id.path_label);
		dir_label = (TextView)findViewById(R.id.dirs_label);
		file_label = (TextView)findViewById(R.id.files_label);
		time_label = (TextView)findViewById(R.id.time_stamp);
		total_label = (TextView)findViewById(R.id.total_size);
		
		/*make zip button visible and setup onclick logic to have
		 * zip button 
		 */
		Button zip = (Button)findViewById(R.id.zip_button);
		zip.setVisibility(Button.GONE);
		
		Button back = (Button)findViewById(R.id.back_button);
		back.setOnClickListener(new ButtonHandler());
		
		new BackgroundWork().execute(path_name);
		
	}
	
	/*
	 * Not finished,
	 * get free and used space of sdcard
	 */

	private class BackgroundWork extends AsyncTask<String, Void, Double> {
		private ProgressDialog dialog;
		private String display_size;
		private int file_count = 0;
		private int dir_count = 0;
		
		protected void onPreExecute(){
			dialog = ProgressDialog.show(DirectoryInfo.this, "", "Calculating information...", true, true);
		}
		
		protected Double doInBackground(String... vals) {
			FileManager flmg = new FileManager();
			File dir = new File(vals[0]);
			File[] list;
			double size;
			int len = 0;
			
			size = flmg.getDirSize(vals[0]);
				
			list = dir.listFiles();
			if(list != null)
				len = list.length;
			
			for (int i = 0; i < len; i++){
				if(list[i].isFile())
					file_count++;
				else if(list[i].isDirectory())
					dir_count++;
			}
			
			if (size > GB)
				display_size = String.format("%.2f Gb ", size / GB);
			else if (size < GB && size > MG)
				display_size = String.format("%.2f Mb ", size / MG);
			else if (size < MG && size > KB)
				display_size = String.format("%.2f Kb ", size/ KB);
			else
				display_size = String.format("%.2f bytes ", size);
			
			return size;
		}
		
		protected void onPostExecute(Double result) {
			File dir = new File(path_name);
			
			name_label.setText(dir.getName());
			path_label.setText(dir.getAbsolutePath());
			dir_label.setText(dir_count + " folders ");
			file_label.setText(file_count + " files ");
			total_label.setText(display_size);
			time_label.setText(new Date(dir.lastModified()) + " ");
			
			dialog.cancel();
		}	
	}
	
	private class ButtonHandler implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.back_button)
				finish();
		}
	}
}
