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
import android.os.StatFs;
import android.os.Environment;
import android.content.Intent;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class DirectoryInfo extends Activity {
	private static final int KB = 1024;
	private static final int MG = KB * KB;
	private static final int GB = MG * KB;
	private String mPathName;
	private TextView mNameLabel, mPathLabel, mDirLabel,
					 mFileLabel, mTimeLabel, mTotalLabel;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_layout);
		
		Intent i = getIntent();
		if(i != null) {
			if(i.getAction() != null && i.getAction().equals(Intent.ACTION_VIEW)) {
				mPathName = i.getData().getPath();
				
				if(mPathName == null)
					mPathName = "";
			} else {
				mPathName = i.getExtras().getString("PATH_NAME");
			}
		}
		
		mNameLabel = (TextView)findViewById(R.id.name_label);
		mPathLabel = (TextView)findViewById(R.id.path_label);
		mDirLabel = (TextView)findViewById(R.id.dirs_label);
		mFileLabel = (TextView)findViewById(R.id.files_label);
		mTimeLabel = (TextView)findViewById(R.id.time_stamp);
		mTotalLabel = (TextView)findViewById(R.id.total_size);
				
		/* make zip button visible and setup onclick logic to have zip button 
		 */
		Button zip = (Button)findViewById(R.id.zip_button);
		zip.setVisibility(Button.GONE);
		 
		
		Button back = (Button)findViewById(R.id.back_button);
		back.setOnClickListener(new ButtonHandler());
		
		new BackgroundWork().execute(mPathName);
		
	}
	


	private class BackgroundWork extends AsyncTask<String, Void, Long> {
		private ProgressDialog dialog;
		private String mDisplaySize;
		private int mFileCount = 0;
		private int mDirCount = 0;
		
		protected void onPreExecute(){
			dialog = ProgressDialog.show(DirectoryInfo.this, "", "Calculating information...", true, true);
		}
		
		protected Long doInBackground(String... vals) {
			FileManager flmg = new FileManager();
			File dir = new File(vals[0]);
			long size = 0;
			int len = 0;

			File[] list = dir.listFiles();
			if(list != null)
				len = list.length;
			
			for (int i = 0; i < len; i++){
				if(list[i].isFile())
					mFileCount++;
				else if(list[i].isDirectory())
					mDirCount++;
			}
			
			if(vals[0].equals("/")) {				
				StatFs fss = new StatFs(Environment.getRootDirectory().getPath());
				size = fss.getAvailableBlocks() * (fss.getBlockSize() / KB);
				
				mDisplaySize = (size > GB) ? 
						String.format("%.2f Gb ", (double)size / MG) :
						String.format("%.2f Mb ", (double)size / KB);
				
			}else if(vals[0].equals("/sdcard")) {
				StatFs fs = new StatFs(Environment.getExternalStorageDirectory()
										.getPath());
				size = fs.getBlockCount() * (fs.getBlockSize() / KB);
				
				mDisplaySize = (size > GB) ? 
					String.format("%.2f Gb ", (double)size / GB) :
					String.format("%.2f Gb ", (double)size / MG);
				
			} else {
				size = flmg.getDirSize(vals[0]);
						
				if (size > GB)
					mDisplaySize = String.format("%.2f Gb ", (double)size / GB);
				else if (size < GB && size > MG)
					mDisplaySize = String.format("%.2f Mb ", (double)size / MG);
				else if (size < MG && size > KB)
					mDisplaySize = String.format("%.2f Kb ", (double)size/ KB);
				else
					mDisplaySize = String.format("%.2f bytes ", (double)size);
			}
			
			return size;
		}
		
		protected void onPostExecute(Long result) {
			File dir = new File(mPathName);
			
			mNameLabel.setText(dir.getName());
			mPathLabel.setText(dir.getAbsolutePath());
			mDirLabel.setText(mDirCount + " folders ");
			mFileLabel.setText(mFileCount + " files ");
			mTotalLabel.setText(mDisplaySize);
			mTimeLabel.setText(new Date(dir.lastModified()) + " ");
			
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
