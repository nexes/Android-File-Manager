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
import java.util.ArrayList;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.util.Log;

/**
 * This class sits between the Main activity and the FileManager class. 
 * To keep the FileManager class modular, this class exists to handle 
 * UI events and communicate that information to the FileManger class
 * 
 * This class is responsible for the buttons onClick method. If one needs
 * to change the functionality of the buttons found from the Main activity
 * or add button logic, this is the class that will need to be edited.
 * 
 * This class is responsible for handling the information that is displayed
 * from the list view (the files and folder) with a a nested class TableRow.
 * The TableRow class is responsible for displaying which icon is shown for each
 * entry. For example a folder will display the folder icon, a Word doc will 
 * display a word icon and so on. If more icons are to be added, the TableRow 
 * class must be updated to display those changes. 
 * 
 * @author Joe Berria
 */
public class EventHandler implements OnClickListener {
	/*
	 * Unique types to control which file operation gets
	 * performed in the background
	 */
    private static final int SEARCH_TYPE =		0x00;
	private static final int COPY_TYPE =		0x01;
	private static final int UNZIP_TYPE =		0x02;
	private static final int UNZIPTO_TYPE =		0x03;
	private static final int ZIP_TYPE =			0x04;
	private static final int DELETE_TYPE = 		0x05;
	private static final int MANAGE_DIALOG =	 0x06;
	
	private final Context context;
	private final FileManager file_mg;
	private TableRow delegate;
	private boolean multi_select_flag = false;
	private boolean thumbnail_flag = true;
	private int color = Color.WHITE;
	
	//the list used to feed info into the array adapter and when multi-select is on
	private ArrayList<String> data_source, multiselect_data;
	private TextView path_label;
	private TextView info_label;

	
	/**
	 * Creates an EventHandler object. This object is used to communicate
	 * most work from the Main activity to the FileManager class.
	 * 
	 * @param context	The context of the main activity e.g  Main
	 * @param manager	The FileManager object that was instantiated from Main
	 */
	public EventHandler(Context context, final FileManager manager) {
		this.context = context;
		file_mg = manager;
		
		data_source = new ArrayList<String>(file_mg.getHomeDir());
	}

	/**
	 * This method is called from the Main activity and this has the same
	 * reference to the same object so when changes are made here or there
	 * they will display in the same way.
	 * 
	 * @param adapter	The TableRow object
	 */
	public void setListAdapter(TableRow adapter) {
		delegate = adapter;
	}
	
	/**
	 * This method is called from the Main activity and is passed
	 * the TextView that should be updated as the directory changes
	 * so the user knows which folder they are in.
	 * 
	 * @param path	The label to update as the directory changes
	 * @param label	the label to update information
	 */
	public void setUpdateLabel(TextView path, TextView label) {
		path_label = path;
		info_label = label;
	}
	
	/**
	 * 
	 * @param color
	 */
	public void setTextColor(int color) {
		this.color = color;
	}
	
	/**
	 * Set this true and thumbnails will be used as the icon for image files. False will
	 * show a default image. 
	 * 
	 * @param show
	 */
	public void setShowThumbnails(boolean show) {
		thumbnail_flag = show;
	}
	
	/**
	 * Indicates whether the user wants to select 
	 * multiple files or folders at a time.
	 * <br><br>
	 * false by default
	 * 
	 * @return	true if the user has turned on multi selection
	 */
	public boolean isMultiSelected() {
		return multi_select_flag;
	}
	
	/**
	 * 
	 * @return	returns true if the user is holding multiple objects (multi-select)
	 */
	public boolean hasMultiSelectData() {
		return (multiselect_data != null && multiselect_data.size() > 0);
	}
	
	/**
	 * Will search for a file then display all files with the 
	 * search parameter in its name
	 * 
	 * @param name	the name to search for
	 */
	public void searchForFile(String name) {
		new BackgroundWork(SEARCH_TYPE).execute(name);
	}
	
	/**
	 * Will delete the file name that is passed on a background
	 * thread.
	 * 
	 * @param name
	 */
	public void deleteFile(String name) {
		new BackgroundWork(DELETE_TYPE).execute(name);
	}
	
	/**
	 * Will copy a file or folder to another location.
	 * 
	 * @param oldLocation	from location
	 * @param newLocation	to location
	 */
	public void copyFile(String oldLocation, String newLocation) {
		String[] data = {oldLocation, newLocation};
		
		new BackgroundWork(COPY_TYPE).execute(data);
	}
	
	/**
	 * 
	 * @param newLocation
	 */
	public void copyFileMultiSelect(String newLocation) {
		String[] data;
		int index = 1;
		
		if (multiselect_data.size() > 0) {
			data = new String[multiselect_data.size() + 1];
			data[0] = newLocation;
			
			for(String s : multiselect_data)
				data[index++] = s;
			
			new BackgroundWork(COPY_TYPE).execute(data);
		}
	}
	
	/**
	 * This will extract a zip file to the same directory.
	 * 
	 * @param file	the zip file name
	 * @param path	the path were the zip file will be extracted (the current directory)
	 */
	public void unZipFile(String file, String path) {
		new BackgroundWork(UNZIP_TYPE).execute(file, path);
	}
	
	/**
	 * This method will take a zip file and extract it to another
	 * location
	 *  
	 * @param name		the name of the of the new file (the dir name is used)
	 * @param newDir	the dir where to extract to
	 * @param oldDir	the dir where the zip file is
	 */
	public void unZipFileToDir(String name, String newDir, String oldDir) {
		new BackgroundWork(UNZIPTO_TYPE).execute(name, newDir, oldDir);
	}
	
	/**
	 * Creates a zip file
	 * 
	 * @param zipPath	the path to the directory you want to zip
	 */
	public void zipFile(String zipPath) {
		new BackgroundWork(ZIP_TYPE).execute(zipPath);
	}

	/**
	 *  This method, handles the button presses of the top buttons found
	 *  in the Main activity. 
	 */
	@Override
	public void onClick(View v) {
		
		switch(v.getId()) {
		
			case R.id.back_button:			
				if (file_mg.getCurrentDir() != "/") {
					if(multi_select_flag) {
						delegate.killMultiSelect(true);
						Toast.makeText(context, "Multi-select is now off", Toast.LENGTH_SHORT).show();
					}
					updateDirectory(file_mg.getPreviousDir());
					path_label.setText(file_mg.getCurrentDir());
				}
				break;
			
			case R.id.home_button:		
				if(multi_select_flag) {
					delegate.killMultiSelect(true);
					Toast.makeText(context, "Multi-select is now off", Toast.LENGTH_SHORT).show();
				}
				updateDirectory(file_mg.getHomeDir());
				if(path_label != null)
					path_label.setText(file_mg.getCurrentDir());
				break;
				
			case R.id.info_button:
				Intent info = new Intent(context, DirectoryInfo.class);
				info.putExtra("PATH_NAME", file_mg.getCurrentDir());
				context.startActivity(info);
				break;
				
			case R.id.help_button:
				Intent help = new Intent(context, HelpManager.class);
				context.startActivity(help);
				break;
				
			case R.id.manage_button:
				display_dialog(MANAGE_DIALOG);
				break;
				
			case R.id.multiselect_button:			
				if(multi_select_flag) {
					delegate.killMultiSelect(true);				
					
				} else {
					LinearLayout hidden_lay = 
						(LinearLayout)((Activity) context).findViewById(R.id.hidden_buttons);
					
					multi_select_flag = true;
					hidden_lay.setVisibility(LinearLayout.VISIBLE);
				}
				break;
			
			/* 
			 * three hidden buttons for multiselect
			 */
			case R.id.hidden_attach:
				/* check if user selected objects before going further */
				if(multiselect_data == null || multiselect_data.isEmpty()) {
					delegate.killMultiSelect(true);
					break;
				}
				
				ArrayList<Uri> uris = new ArrayList<Uri>();
				int length = multiselect_data.size();
				Intent mail_int = new Intent();
    			
    			mail_int.setAction(android.content.Intent.ACTION_SEND_MULTIPLE);
    			mail_int.setType("application/mail");
    			mail_int.putExtra(Intent.EXTRA_BCC, "");
    			mail_int.putExtra(Intent.EXTRA_SUBJECT, " ");
    			
    			for(int i = 0; i < length; i++) {
    				File file = new File(multiselect_data.get(i));
    				uris.add(Uri.fromFile(file));
    			}
    			
    			mail_int.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
    			context.startActivity(Intent.createChooser(mail_int, 
    													   "Email using..."));
    			
    			delegate.killMultiSelect(true);
				break;
				
			case R.id.hidden_copy:
				/* check if user selected objects before going further */
				if(multiselect_data == null || multiselect_data.isEmpty()) {
					delegate.killMultiSelect(true);
					break;
				}
				
				info_label.setText("Holding " + multiselect_data.size() + 
								   " file(s) to be copied");
				
				delegate.killMultiSelect(false);
				break;
				
			case R.id.hidden_delete:	
				/* check if user selected objects before going further */
				if(multiselect_data == null || multiselect_data.isEmpty()) {
					delegate.killMultiSelect(true);
					break;
				}

				String[] data = new String[multiselect_data.size()];
				int at = 0;
				
				for(String string : multiselect_data)
					data[at++] = string;
				
				new BackgroundWork(DELETE_TYPE).execute(data);
				delegate.killMultiSelect(true);
				break;
		}
	}
	
	/**
	 * will return the data in the ArrayList that holds the dir contents. 
	 * 
	 * @param position	the indext of the arraylist holding the dir content
	 * @return the data in the arraylist at position (position)
	 */
	public String getData(int position) {
		
		if(position > data_source.size() - 1 || position < 0)
			return null;
		
		return data_source.get(position);
	}

	/**
	 * called to update the file contents as the user navigates there
	 * phones file system. 
	 * 
	 * @param content	an ArrayList of the file/folders in the current directory.
	 */
	public void updateDirectory(ArrayList<String> content) {	
		if(!data_source.isEmpty())
			data_source.clear();
		
		for(String data : content)
			data_source.add(data);
		
		delegate.notifyDataSetChanged();
	}

	/**
	 * This private method is used to display options the user can select when
	 * the tool box button is pressed. The WIFI option is commented out as it doesn't
	 * seem to fit with the overall idea of the application. However to display it, just 
	 * uncomment the below code and the code in the AndroidManifest.xml file. 
	 */
	private void display_dialog(int type) {
		AlertDialog.Builder builder;
    	AlertDialog dialog;
    	
    	switch(type) {
    		case MANAGE_DIALOG:
    			//un-comment WIFI Info here and in the manifest file 
    	    	//to display WIFI info. Also uncomment and change case number below
    	    	CharSequence[] options = {"Process Info", /*"Wifi Info",*/ "Application backup"};
    	    	
    	    	builder = new AlertDialog.Builder(context);
    	    	builder.setTitle("Tool Box");
    	    	builder.setIcon(R.drawable.toolbox);
    	    	builder.setItems(options, new DialogInterface.OnClickListener() {
    	    		
    				public void onClick(DialogInterface dialog, int index) {
    					Intent i;
    					
    					switch(index) {
    						case 0:
    							i = new Intent(context, ProcessManager.class);
    							context.startActivity(i);
    							break;
    	/*
    						case 1:
    							i = new Intent(context, WirelessManager.class);
    							context.startActivity(i);
    							break;
    	*/
    						case 1:
    							i = new Intent(context, ApplicationBackup.class);
    							context.startActivity(i);
    							break;
    					}
    				}
    			});
    	    	dialog = builder.create();
    	    	dialog.show();
    			break;
    	}
	}
	
	private static class ViewHolder {
		TextView topView;
		TextView bottomView;
		ImageView icon;
		ImageView mSelect;	//multi-select check mark icon
	}

	
	/**
	 * A nested class to handle displaying a custom view in the ListView that
	 * is used in the Main activity. If any icons are to be added, they must
	 * be implemented in the getView method. This class is instantiated once in Main
	 * and has no reason to be instantiated again. 
	 * 
	 * @author Joe Berria
	 */
    public class TableRow extends ArrayAdapter<String> {
    	private final int KB = 1024;
    	private final int MG = KB * KB;
    	private final int GB = MG * KB;
    	private String display_size;
    	private String dir_name = "/sdcard";
    	private ArrayList<Integer> positions;
    	private LinearLayout hidden_layout;
    	private ThumbnailCreator thumbnail;
    	
    	public TableRow() {
    		super(context, R.layout.tablerow, data_source);
    		
    		thumbnail = new ThumbnailCreator(32, 32);
    	}
    	
    	public void addMultiPosition(int index, String path) {
    		if(positions == null)
    			positions = new ArrayList<Integer>();
    		
    		if(multiselect_data == null) {
    			positions.add(index);
    			add_multiSelect_file(path);
    			
    		} else if(multiselect_data.contains(path)) {
    			if(positions.contains(index))
    				positions.remove(new Integer(index));
    			
    			multiselect_data.remove(path);
    			
    		} else {
    			positions.add(index);
    			add_multiSelect_file(path);
    		}
    		
    		notifyDataSetChanged();
    	}
   	
    	/**
    	 * This will turn off multi-select and hide the multi-select buttons at the
    	 * bottom of the view. 
    	 * 
    	 * @param clearData if this is true any file/folder the user selected for multi-select
    	 * 					will be cleared. If false, the data will be kept for later use. Note:
    	 * 					multi-select copy will usually be the only one to pass true, so we can
    	 * 					later paste it to another folder.
    	 */
    	public void killMultiSelect(boolean clearData) {
    		hidden_layout = (LinearLayout)((Activity)context).findViewById(R.id.hidden_buttons);
    		hidden_layout.setVisibility(LinearLayout.GONE);
    		multi_select_flag = false;
    		
    		if(positions != null && !positions.isEmpty())
    			positions.clear();
    		
    		if(clearData)
    			if(multiselect_data != null && !multiselect_data.isEmpty())
    				multiselect_data.clear();
    		
    		notifyDataSetChanged();
    	}
    	
    	public String getFilePermissions(File file) {
    		String per = "-";
    		
    		if(file.isDirectory())
    			per += "d";
    		if(file.canRead())
    			per += "r";
    		if(file.canWrite())
    			per += "w";
    		
    		return per;
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		ViewHolder holder;
    		int num_items = 0;
    		String temp = file_mg.getCurrentDir();
    		File file = new File(temp + "/" + data_source.get(position));
    		String[] list = file.list();
    		
    		if(list != null)
    			num_items = list.length;
   
    		if(convertView == null) {
    			LayoutInflater inflater = (LayoutInflater) context.
    						getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			convertView = inflater.inflate(R.layout.tablerow, parent, false);
    			
    			holder = new ViewHolder();
    			holder.topView = (TextView)convertView.findViewById(R.id.top_view);
    			holder.bottomView = (TextView)convertView.findViewById(R.id.bottom_view);
    			holder.icon = (ImageView)convertView.findViewById(R.id.row_image);
    			holder.mSelect = (ImageView)convertView.findViewById(R.id.multiselect_icon);
    			
    			convertView.setTag(holder);
    			
    		} else {
    			holder = (ViewHolder)convertView.getTag();
    		}
    		
    		/* This will check if the thumbnail cache needs to be cleared by checking
    		 * if the user has changed directories. This way the cache wont show
    		 * a wrong thumbnail image for the new image file 
    		 */
    		if(!dir_name.equals(temp) && thumbnail_flag) {
    			thumbnail.clearBitmapCache();
    			dir_name = temp;
    		}    			
    		  		
    		if (positions != null && positions.contains(position))
    			holder.mSelect.setVisibility(ImageView.VISIBLE);
    		else
    			holder.mSelect.setVisibility(ImageView.GONE);

    		holder.topView.setTextColor(color);
    		holder.bottomView.setTextColor(color);
    		
    		if(file.isDirectory()) {
    			holder.icon.setImageResource(R.drawable.folder);
    			
    		} else if(file.isFile()) {
    			String ext = file.toString();
    			String sub_ext = ext.substring(ext.lastIndexOf(".") + 1);
    			
    			/*This series of else if statements will determine which icon is displayed*/
    			if (sub_ext.equalsIgnoreCase("pdf")) {
    				holder.icon.setImageResource(R.drawable.pdf);
    			
    			} else if (sub_ext.equalsIgnoreCase("mp3") || 
    					   sub_ext.equalsIgnoreCase("wma") || 
    					   sub_ext.equalsIgnoreCase("m4a") || 
    					   sub_ext.equalsIgnoreCase("m4p")) {
    				
    				holder.icon.setImageResource(R.drawable.music);
    			
    			} else if (sub_ext.equalsIgnoreCase("png") ||
    					   sub_ext.equalsIgnoreCase("jpg") ||
    					   sub_ext.equalsIgnoreCase("jpeg")|| 
    					   sub_ext.equalsIgnoreCase("gif") ||
    					   sub_ext.equalsIgnoreCase("tiff")) {
    				
    				if(thumbnail_flag) {
	    				Bitmap thumb = thumbnail.hasBitmapCached(position);
	    				
	    				if(thumb == null) {  					
	    					final Handler mHandler = new Handler();
	   						thumbnail.setBitmapToImageView(file.getPath(), 
	   													   mHandler, 
	   													   holder.icon);
	   						
	    				} else {
	    					holder.icon.setImageBitmap(thumb);
	    				}
	    				
    				} else {
    					holder.icon.setImageResource(R.drawable.image);
    				}
    							
    			} else if (sub_ext.equalsIgnoreCase("zip")  || 
    					   sub_ext.equalsIgnoreCase("gzip") ||
    					   sub_ext.equalsIgnoreCase("gz")) {
    				
    				holder.icon.setImageResource(R.drawable.zip);
    			
    			} else if(sub_ext.equalsIgnoreCase("m4v") ||
    					  sub_ext.equalsIgnoreCase("wmv") ||
    					  sub_ext.equalsIgnoreCase("3gp") || 
    					  sub_ext.equalsIgnoreCase("mp4")) {
    				
    				holder.icon.setImageResource(R.drawable.movies);
    			
    			} else if(sub_ext.equalsIgnoreCase("doc") || 
    					  sub_ext.equalsIgnoreCase("docx")) {
    				
    				holder.icon.setImageResource(R.drawable.word);
    			
    			} else if(sub_ext.equalsIgnoreCase("xls") || 
    					  sub_ext.equalsIgnoreCase("xlsx")) {
    				
    				holder.icon.setImageResource(R.drawable.excel);
    				
    			} else if(sub_ext.equalsIgnoreCase("ppt") ||
    					  sub_ext.equalsIgnoreCase("pptx")) {
    				
    				holder.icon.setImageResource(R.drawable.ppt);   	
    				
    			} else if(sub_ext.equalsIgnoreCase("html")) {
    				holder.icon.setImageResource(R.drawable.html32);  
    				
    			} else if(sub_ext.equalsIgnoreCase("xml")) {
    				holder.icon.setImageResource(R.drawable.xml32);
    				
    			} else if(sub_ext.equalsIgnoreCase("conf")) {
    				holder.icon.setImageResource(R.drawable.config32);
    				
    			} else if(sub_ext.equalsIgnoreCase("apk")) {
    				holder.icon.setImageResource(R.drawable.appicon);
    				
    			} else if(sub_ext.equalsIgnoreCase("jar")) {
    				holder.icon.setImageResource(R.drawable.jar32);
    				
    			} else {
    				holder.icon.setImageResource(R.drawable.text);
    			}
    		}
    		    		
    		String permission = getFilePermissions(file);
    		
    		if(file.isFile()) {
    			double size = file.length();
        		if (size > GB)
    				display_size = String.format("%.2f Gb ", (double)size / GB);
    			else if (size < GB && size > MG)
    				display_size = String.format("%.2f Mb ", (double)size / MG);
    			else if (size < MG && size > KB)
    				display_size = String.format("%.2f Kb ", (double)size/ KB);
    			else
    				display_size = String.format("%.2f bytes ", (double)size);
        		
        		if(file.isHidden())
        			holder.bottomView.setText("(hidden) | " + display_size +" | "+ permission);
        		else
        			holder.bottomView.setText(display_size +" | "+ permission);
        		
    		} else {
    			if(file.isHidden())
    				holder.bottomView.setText("(hidden) | " + num_items + " items | " + permission);
    			else
    				holder.bottomView.setText(num_items + " items | " + permission);
    		}
    		
    		holder.topView.setText(file.getName());
    		
    		return convertView;
    	}
    	
    	private void add_multiSelect_file(String src) {
    		if(multiselect_data == null)
    			multiselect_data = new ArrayList<String>();
    		
    		multiselect_data.add(src);
    	}
    }
    
    /**
     * A private inner class of EventHandler used to perform time extensive 
     * operations. So the user does not think the the application has hung, 
     * operations such as copy/past, search, unzip and zip will all be performed 
     * in the background. This class extends AsyncTask in order to give the user
     * a progress dialog to show that the app is working properly.
     * 
     * (note): this class will eventually be changed from using AsyncTask to using
     * Handlers and messages to perform background operations. 
     * 
     * @author Joe Berria
     */
    private class BackgroundWork extends AsyncTask<String, Void, ArrayList<String>> {
    	private String file_name;
    	private ProgressDialog pr_dialog;
    	private int type;
    	private int copy_rtn;
    	
    	private BackgroundWork(int type) {
    		this.type = type;
    	}
    	
    	/**
    	 * This is done on the EDT thread. this is called before 
    	 * doInBackground is called
    	 */
    	@Override
    	protected void onPreExecute() {
    		
    		switch(type) {
    			case SEARCH_TYPE:
    				pr_dialog = ProgressDialog.show(context, "Searching", 
    												"Searching current file system...",
    												true, true);
    				break;
    				
    			case COPY_TYPE:
    				pr_dialog = ProgressDialog.show(context, "Copying", 
    												"Copying file...", 
    												true, false);
    				break;
    				
    			case UNZIP_TYPE:
    				pr_dialog = ProgressDialog.show(context, "Unzipping", 
    												"Unpacking zip file please wait...",
    												true, false);
    				break;
    				
    			case UNZIPTO_TYPE:
    				pr_dialog = ProgressDialog.show(context, "Unzipping", 
    												"Unpacking zip file please wait...",
    												true, false);
    				break;
    			
    			case ZIP_TYPE:
    				pr_dialog = ProgressDialog.show(context, "Zipping", 
    												"Zipping folder...", 
    												true, false);
    				break;
    				
    			case DELETE_TYPE:
    				pr_dialog = ProgressDialog.show(context, "Deleting", 
    												"Deleting files...", 
    												true, false);
    				break;
    		}
    	}

    	/**
    	 * background thread here
    	 */
    	@Override
		protected ArrayList<String> doInBackground(String... params) {
			
			switch(type) {
				case SEARCH_TYPE:
					file_name = params[0];
					ArrayList<String> found = file_mg.searchInDirectory(file_mg.getCurrentDir(), 
																	    file_name);
					return found;
					
				case COPY_TYPE:
					int len = params.length;
					
					if(multiselect_data != null && !multiselect_data.isEmpty()) {
						for(int i = 1; i < len; i++)
							copy_rtn = file_mg.copyToDirectory(params[i], params[0]);	
					} else {
						copy_rtn = file_mg.copyToDirectory(params[0], params[1]);
					}
					
					return null;
					
				case UNZIP_TYPE:
					file_mg.extractZipFiles(params[0], params[1]);
					return null;
					
				case UNZIPTO_TYPE:
					file_mg.extractZipFilesFromDir(params[0], params[1], params[2]);
					return null;
					
				case ZIP_TYPE:
					file_mg.createZipFile(params[0]);
					return null;
					
				case DELETE_TYPE:
					int size = params.length;
					
					for(int i = 0; i < size; i++)
						file_mg.deleteTarget(params[i]);
					
					return null;
			}
			return null;
		}
		
    	/**
    	 * This is called when the background thread is finished. Like onPreExecute, anything
    	 * here will be done on the EDT thread. 
    	 */
    	@Override
		protected void onPostExecute(final ArrayList<String> file) {			
			final CharSequence[] names;
			int len = file != null ? file.size() : 0;
			
			switch(type) {
				case SEARCH_TYPE:				
					if(len == 0) {
						Toast.makeText(context, "Couldn't find " + file_name, 
											Toast.LENGTH_SHORT).show();
					
					} else {
						names = new CharSequence[len];
						
						for (int i = 0; i < len; i++) {
							String entry = file.get(i);
							names[i] = entry.substring(entry.lastIndexOf("/") + 1, entry.length());
						}
						
						AlertDialog.Builder builder = new AlertDialog.Builder(context);
						builder.setTitle("Found " + len + " file(s)");
						builder.setItems(names, new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int position) {
								String path = file.get(position);
								updateDirectory(file_mg.getNextDir(path.
													substring(0, path.lastIndexOf("/")), true));
							}
						});
						
						AlertDialog dialog = builder.create();
						dialog.show();
					}
					
					pr_dialog.dismiss();
					break;
					
				case COPY_TYPE:
					if(multiselect_data != null && !multiselect_data.isEmpty()) {
						multi_select_flag = false;
						multiselect_data.clear();
					}
					
					if(copy_rtn == 0)
						Toast.makeText(context, "File successfully copied and pasted", 
											Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(context, "Copy pasted failed", Toast.LENGTH_SHORT).show();
					
					pr_dialog.dismiss();
					info_label.setText("");
					break;
					
				case UNZIP_TYPE:
					updateDirectory(file_mg.getNextDir(file_mg.getCurrentDir(), true));
					pr_dialog.dismiss();
					break;
					
				case UNZIPTO_TYPE:
					updateDirectory(file_mg.getNextDir(file_mg.getCurrentDir(), true));
					pr_dialog.dismiss();
					break;
					
				case ZIP_TYPE:
					updateDirectory(file_mg.getNextDir(file_mg.getCurrentDir(), true));
					pr_dialog.dismiss();
					break;
					
				case DELETE_TYPE:
					if(multiselect_data != null && !multiselect_data.isEmpty()) {
						multiselect_data.clear();
						multi_select_flag = false;
					}
					
					updateDirectory(file_mg.getNextDir(file_mg.getCurrentDir(), true));
					pr_dialog.dismiss();
					info_label.setText("");
					break;
			}
		}
    }
}
