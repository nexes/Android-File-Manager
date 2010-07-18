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
import java.util.Arrays;
import java.util.Comparator;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.util.Log;

/**
 * 
 * @author Joe Berria
 *
 */
public class EventHandler implements OnClickListener {
	private static final int SEARCH_TYPE =	0;
	private static final int COPY_TYPE =	1;
	private static final int UNZIP_TYPE =	2;
	private static final int UNZIPTO_TYPE =	3;
	private static final int ZIP_TYPE =		4;

	private final Context context;
	private final FileManager file_mg;
	private TableRow delegate;
	private ArrayList<String> data_source;
	
	/*
	 * 
	 */
	private static final Comparator alph = new Comparator<String>() {
		@Override
		public int compare(String arg0, String arg1) {
			return arg0.toLowerCase().compareTo(arg1.toLowerCase());
		}
	};
	
	/*
	 * 
	 */
	private static final Comparator type = new Comparator<String>() {
		@Override
		public int compare(String arg0, String arg1) {
			String ext = null;
			String ext2 = null;
			
			try {
				ext = arg0.substring(arg0.lastIndexOf(".") + 1, arg0.length());
				ext2 = arg1.substring(arg1.lastIndexOf(".") + 1, arg1.length());
				
			} catch (IndexOutOfBoundsException e) {
				return 0;
			}
			
			return ext.compareTo(ext2);
		}
	};

	/**
	 * 	
	 * @param context
	 * @param manager
	 */
	public EventHandler(Context context, final FileManager manager) {
		this.context = context;
		file_mg = manager;
		
		data_source = new ArrayList<String>(file_mg.getHomeDir());
	}
	
	/**
	 * 
	 * @param adapter
	 */
	public void setListAdapter(TableRow adapter) {
		delegate = adapter;
	}
	
	/**
	 * 
	 * @param name
	 */
	public void searchForFile(String name) {
		new BackgroundSearch(SEARCH_TYPE).execute(name);
	}
	
	/**
	 * 
	 * @param oldLocation
	 * @param newLocation
	 */
	public void copyFile(String oldLocation, String newLocation) {
		new BackgroundSearch(COPY_TYPE).execute(oldLocation, newLocation);
	}
	
	/**
	 * 
	 * @param file
	 * @param path
	 */
	public void unZipFile(String file, String path) {
		new BackgroundSearch(UNZIP_TYPE).execute(file, path);
	}
	
	/**
	 * 
	 * @param name
	 * @param newDir
	 * @param oldDir
	 */
	public void unZipFileToDir(String name, String newDir, String oldDir) {
		new BackgroundSearch(UNZIPTO_TYPE).execute(name, newDir, oldDir);
	}
	
	/**
	 * 
	 * @param zipPath
	 */
	public void zipFile(String zipPath) {
		new BackgroundSearch(ZIP_TYPE).execute(zipPath);
	}

	/**
	 * 
	 */
	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.back_button) {
			if (file_mg.getCurrentDir() != "/") 
				updateDirectory(file_mg.getPreviousDir());
			
		} else if(v.getId() == R.id.home_button) {
			updateDirectory(file_mg.getHomeDir());
				
		} else if(v.getId() == R.id.info_button) {
			Intent i = new Intent(context, DirectoryInfo.class);
			i.putExtra("PATH_NAME", file_mg.getCurrentDir());
			context.startActivity(i);
				
		}else if(v.getId() == R.id.help_button) {
			//stub
			
		} else if(v.getId() == R.id.manage_button)
			display_dialog();
	}
	
	/**
	 * 
	 * @param position
	 * @return
	 */
	public String getData(int position) {
		
		if(position > data_source.size() - 1 || position < 0)
			return null;
		
		return data_source.get(position);
	}
	
	/**
	 * 
	 */
	public void sortAlphabetical() {
		ArrayList<String> list;
		Object[] t = data_source.toArray();
		
		Arrays.sort(t, alph);
		list = new ArrayList<String>();
		
		for (Object a : t){
			list.add((String)a);
		}
		
		updateDirectory(list);
	}
	
	public void sortByType() {
		ArrayList<String> list;
		Object[] t = data_source.toArray();
		String dir = file_mg.getCurrentDir();
		
		Arrays.sort(t, type);
		list = new ArrayList<String>();
		
		for (Object a : t){
			if(new File(dir + "/" + (String)a).isDirectory())
				list.add(0, (String)a);
			else
				list.add((String)a);
		}
		
		updateDirectory(list);
	}

	/**
	 * 
	 * @param content
	 */
	public void updateDirectory(ArrayList<String> content) {
		int len = content.size();
		
		if(!data_source.isEmpty())
			data_source.clear();
		
		for(int i = 0; i < len; i++)
			data_source.add(content.get(i));
		
		delegate.notifyDataSetChanged();
	}

	/**
	 * 
	 */
	private void display_dialog() {
		AlertDialog.Builder builder;
    	AlertDialog dialog;
    	
    	//un-comment Wifi Info here and in the manafest file 
    	//to display Wifi info. Also uncomment and change case number below
    	CharSequence[] options = {"Process Info", /*"Wifi Info",*/ "Application backup"};
    	
    	builder = new AlertDialog.Builder(context);
    	builder.setTitle("ToolBox");
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
	}
    
	/**
	 * A nested class to handle displaying a custom view in the ListView 
	 * of the Main activity.
	 * @author Joe Berria
	 *
	 */
    public class TableRow extends ArrayAdapter<String> {
    	private final int KB = 1024;
    	private final int MG = KB * KB;
    	private final int GB = MG * KB;
    	private String display_size;
    	
    	/**
    	 * 
    	 */
    	public TableRow() {
    		super(context, R.layout.tablerow, data_source);
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		File file;
    		View view = convertView;
   
    		if(view == null) {
    			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			view = inflater.inflate(R.layout.tablerow, parent, false);
    		}
    		
    		TextView top = (TextView)view.findViewById(R.id.top_view);
    		TextView bottom = (TextView)view.findViewById(R.id.bottom_view);
    		ImageView icon = (ImageView)view.findViewById(R.id.row_image);
    		
    		String temp = file_mg.getCurrentDir();
    		file = new File(temp + "/" + data_source.get(position));
    		
    		if(file.isFile()) {
    			String ext = file.toString();
    			String sub_ext = ext.substring(ext.lastIndexOf(".") + 1);
    			
    			/*This series of if-else if statements will determine which icon is displayed*/
    			if (sub_ext.equalsIgnoreCase("pdf")) 
    				icon.setImageResource(R.drawable.pdf);
    			
    			else if (sub_ext.equalsIgnoreCase("mp3") || sub_ext.equalsIgnoreCase("wma") || 
    					 sub_ext.equalsIgnoreCase("m4a") || sub_ext.equalsIgnoreCase("m4p"))
    				icon.setImageResource(R.drawable.music);
    			
    			else if (sub_ext.equalsIgnoreCase("png") || sub_ext.equalsIgnoreCase("jpg") ||
    					 sub_ext.equalsIgnoreCase("jpeg") || sub_ext.equalsIgnoreCase("gif")||
    					 sub_ext.equalsIgnoreCase("tiff"))
    				icon.setImageResource(R.drawable.image);
    			
    			else if (sub_ext.equalsIgnoreCase("zip") || sub_ext.equalsIgnoreCase("gzip") ||
    					 sub_ext.equalsIgnoreCase("gz"))
    				icon.setImageResource(R.drawable.zip);
    			
    			else if(sub_ext.equalsIgnoreCase("m4v") || sub_ext.equalsIgnoreCase("wmv") ||
    					sub_ext.equalsIgnoreCase("3gp") || sub_ext.equalsIgnoreCase("mp4"))
    				icon.setImageResource(R.drawable.movies);
    			
    			else if(sub_ext.equalsIgnoreCase("doc") || sub_ext.equalsIgnoreCase("docx"))
    				icon.setImageResource(R.drawable.word);
    			
    			else if(sub_ext.equalsIgnoreCase("xls") || sub_ext.equalsIgnoreCase("xlsx"))
    				icon.setImageResource(R.drawable.excel);
    			
    			else if(sub_ext.equalsIgnoreCase("ppt") || sub_ext.equalsIgnoreCase("pptx"))
    				icon.setImageResource(R.drawable.ppt);
    			
    			else if(sub_ext.equalsIgnoreCase("html"))
    				icon.setImageResource(R.drawable.html32);
    			
    			else if(sub_ext.equalsIgnoreCase("xml"))
    				icon.setImageResource(R.drawable.xml32);
    			
    			else if(sub_ext.equalsIgnoreCase("conf"))
    				icon.setImageResource(R.drawable.config32);
    			
    			else if(sub_ext.equalsIgnoreCase("apk"))
    				icon.setImageResource(R.drawable.appicon);
    			
    			else if(sub_ext.equalsIgnoreCase("jar"))
    				icon.setImageResource(R.drawable.jar32);
    			
    			else
    				icon.setImageResource(R.drawable.text);
    			
    		} else if(file.isDirectory()) {
    				icon.setImageResource(R.drawable.folder);
    		}
    		
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
    			bottom.setText("(hidden)  " + display_size);
    		else
    			bottom.setText(display_size);
    		
    		top.setText(file.getName());
    		return view;
    	}
    }
    
    /**
     * 
     * @author Joe Berria
     *
     */
    private class BackgroundSearch extends AsyncTask<String, Void, ArrayList<String>> {
    	private String file_name;
    	private ProgressDialog pr_dialog;
    	private int type;
    	private int copy_rtn;
    	
    	/**
    	 * 
    	 * @param type
    	 */
    	private BackgroundSearch(int type) {
    		this.type = type;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		
    		switch(type) {
    			case 0:	/* SEARCH */
    				pr_dialog = ProgressDialog.show(context, "Searching", "Searching current file system...",
    												true, true);
    				break;
    				
    			case 1:	/* COPY */
    				pr_dialog = ProgressDialog.show(context, "Copying", "Copying file...", true, false);
    				break;
    				
    			case 2:	/* UNZIP */
    				pr_dialog = ProgressDialog.show(context, "Unzipping", "Unpacking zip file please wait...",
    												true, false);
    				break;
    				
    			case 3: /* UNZIPTO */
    				pr_dialog = ProgressDialog.show(context, "Unzipping", "Unpacking zip file please wait...",
    												true, false);
    				break;
    			
    			case 4: /* ZIP */
    				pr_dialog = ProgressDialog.show(context, "Zipping", "Zipping folder...", true, false);
    				break;
    		}
    	}

    	@Override
		protected ArrayList<String> doInBackground(String... params) {
			
			switch(type) {
				case 0:
					file_name = params[0];
					ArrayList<String> found = file_mg.searchInDirectory(file_mg.getCurrentDir(), file_name);
					return found;
					
				case 1:
					copy_rtn = file_mg.copyToDirectory(params[0], params[1]);
					return null;
					
				case 2:
					file_mg.extractZipFiles(params[0], params[1]);
					return null;
					
				case 3:
					file_mg.extractZipFilesFromDir(params[0], params[1], params[2]);
					return null;
					
				case 4:
					file_mg.createZipFile(params[0]);
					return null;
			}
			
			return null;
		}
		
    	@Override
		protected void onPostExecute(final ArrayList<String> file) {			
			final CharSequence[] names;
			int len = file != null ? file.size() : 0;
			
			switch(type) {
				case 0:	/* SEARCH */					
					if(len == 0) {
						Toast.makeText(context, "Couldn't find " + file_name, Toast.LENGTH_SHORT).show();
					
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
								updateDirectory(file_mg.getNextDir(path.substring(0, path.lastIndexOf("/")), true));
							}
						});
						
						AlertDialog dialog = builder.create();
						dialog.show();
					}
					
					pr_dialog.dismiss();
					break;
					
				case 1: /* COPY */
					if(copy_rtn == 0)
						Toast.makeText(context, "File successfully copied and pasted", Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(context, "Copy pasted failed", Toast.LENGTH_SHORT).show();
					
					pr_dialog.dismiss();
					break;
					
				case 2:	/* UNZIP */
					updateDirectory(file_mg.getNextDir(file_mg.getCurrentDir(), true));
					pr_dialog.dismiss();
					break;
					
				case 3: /* UNZIPTO */
					updateDirectory(file_mg.getNextDir(file_mg.getCurrentDir(), true));
					pr_dialog.dismiss();
					break;
					
				case 4: /* ZIP */
					updateDirectory(file_mg.getNextDir(file_mg.getCurrentDir(), true));
					pr_dialog.dismiss();
					break;
			}
		}
    }
}
