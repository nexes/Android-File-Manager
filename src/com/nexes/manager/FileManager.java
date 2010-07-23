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

import android.util.Log;

import java.util.ArrayList;
import java.util.Stack;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 
 * @author Joe Berria
 *
 */
public class FileManager {
	
	private static final int BUFFER = 2048;
	private boolean show_hidden = false;
	private double dir_size = 0;
	private Stack<String> path_stack;
	private ArrayList<String> dir_content;
	
	/**
	 * 
	 */
	public FileManager() {
		dir_content = new ArrayList<String>();
		path_stack = new Stack<String>();
		
		path_stack.push("/");
		path_stack.push(path_stack.peek() + "sdcard");
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCurrentDir() {
		return path_stack.peek();
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getHomeDir() {
		path_stack.clear();
		path_stack.push("/");
		path_stack.push(path_stack.peek() + "sdcard");
		
		return populate_list();
	}
	
	/**
	 * 
	 * @param choice
	 */
	public void setShowHiddenFiles(boolean choice) {
		show_hidden = choice;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getPreviousDir() {
		int size = path_stack.size();
		
		if (size >= 2)
			path_stack.pop();
		
		else if(size == 0)
			path_stack.push("/");
		
		return populate_list();
	}
	
	/**
	 * 
	 * @param path
	 * @param isFullPath
	 * @return
	 */
	public ArrayList<String> getNextDir(String path, boolean isFullPath) {
		int size = path_stack.size();
		
		if(!path.equals(path_stack.peek()) && !isFullPath) {
			if(size == 1)
				path_stack.push("/" + path);
			else
				path_stack.push(path_stack.peek() + "/" + path);
		}
		
		else if(!path.equals(path_stack.peek()) && isFullPath) {
			path_stack.push(path);
		}
		
		return populate_list();
	}

	/**
	 * 
	 * @param old
	 * @param newDir
	 * @return
	 */
	public int copyToDirectory(String old, String newDir) {
		File old_file = new File(old);
		File temp_dir = new File(newDir);
		byte[] data = new byte[BUFFER];
		int read = 0;
		
		if(old_file.isFile() && temp_dir.isDirectory() && temp_dir.canWrite()){
			String file_name = old.substring(old.lastIndexOf("/"), old.length());
			File cp_file = new File(newDir + file_name);
			try {
				BufferedOutputStream o_stream = new BufferedOutputStream(new FileOutputStream(cp_file));
				BufferedInputStream i_stream = new BufferedInputStream(new FileInputStream(old_file));
				
				while((read = i_stream.read(data, 0, BUFFER)) != -1)
					o_stream.write(data, 0, read);
				
				o_stream.flush();
				i_stream.close();
				o_stream.close();
				
			} catch (FileNotFoundException e) {
				Log.e("FileNotFoundException", e.getMessage());
				return -1;
				
			} catch (IOException e) {
				Log.e("IOException", e.getMessage());
				return -1;
			}
			
		}else if(old_file.isDirectory() && temp_dir.isDirectory() && temp_dir.canWrite()) {
			String files[] = old_file.list();
			String dir = newDir + old.substring(old.lastIndexOf("/"), old.length());
			int len = files.length;
			
			if(!new File(dir).mkdir())
				return -1;
			
			for(int i = 0; i < len; i++)
				copyToDirectory(old + "/" + files[i], dir);
			
		} else if(!temp_dir.canWrite())
			return -1;
		
		return 0;
	}
	
	/**
	 * 
	 * @param toDir
	 * @param fromDir
	 */
	public void extractZipFilesFromDir(String zipName, String toDir, String fromDir) {
		byte[] buff = new byte[BUFFER];
		String _directory;
		BufferedInputStream in_stream = null;
		BufferedOutputStream out_stream = null;
		File dest_file;
		ZipEntry entry;
		int len;
		
		_directory = toDir + zipName.substring(0, zipName.lastIndexOf(".zip")) + "/";
		new File(_directory).mkdir();
		
		if(fromDir.charAt(fromDir.length() - 1) != '/')
			fromDir += "/";
		
		try {
			ZipFile zips = new ZipFile(fromDir + zipName);
			Enumeration<?> files = zips.entries();

			while (files.hasMoreElements()) {
			   entry = (ZipEntry) files.nextElement();
			   String name = entry.getName();
			   			   
		   	   if(entry.isDirectory()) {
				File new_dir = new File(_directory + name);
				
				Log.e("NEW DIR", _directory + name);
				
				new_dir.mkdir();
				
			   } else {
				in_stream = new BufferedInputStream(zips.getInputStream(entry));
				dest_file = new File(_directory + name.substring(name.lastIndexOf("/"), name.length()));
				out_stream = new BufferedOutputStream(new FileOutputStream(dest_file));
				
				while((len = in_stream.read(buff, 0, BUFFER)) != -1)
					out_stream.write(buff, 0, len);
				
				out_stream.flush();
				out_stream.close();
				in_stream.close();
			   }	
			}
		 }catch (IOException e) {
		    Log.e("ZIP ERROR", e.toString());
		 }
	}
	
	/**
	 * 
	 * @param zip_file
	 * @param directory
	 */
	public void extractZipFiles(String zip_file, String directory) {
		byte[] buff = new byte[BUFFER];
		String _directory;
		BufferedInputStream in_stream = null;
		BufferedOutputStream out_stream = null;
		File dest_file;
		ZipEntry entry;
		int len;

		File check = new File(directory);
		if(!check.canRead() || !check.canWrite())
			return;
		
		_directory = directory + zip_file.substring(0, zip_file.lastIndexOf(".zip")) + "/";
		
		new File(_directory).mkdir();
		
		try {
			ZipFile zips = new ZipFile(directory + zip_file);
			Enumeration<?> files = zips.entries();

			while (files.hasMoreElements()) {
			   entry = (ZipEntry) files.nextElement();
			   String name = entry.getName();
			   
			   Log.e("unzipping file", name);
			   
		   	   if(entry.isDirectory()) {
		   		   new File(_directory + name).mkdir();
								
			   } else {
				in_stream = new BufferedInputStream(zips.getInputStream(entry));
				dest_file = new File(_directory + name.substring(name.lastIndexOf("/"), name.length()));
				out_stream = new BufferedOutputStream(new FileOutputStream(dest_file));
				
				while((len = in_stream.read(buff, 0, BUFFER)) != -1)
					out_stream.write(buff, 0, len);
				
				out_stream.flush();
				out_stream.close();
				in_stream.close();
			   }
			}
		 }catch (IOException e) {
		    Log.e("ZIP ERROR", e.toString());
		 }
	}
	
	/**
	 * 
	 * @param path
	 */
	public void createZipFile(String path) {
		File dir = new File(path);
		String[] list = dir.list();
		String name = path.substring(path.lastIndexOf("/"), path.length());
		String _path;
		
		if(!dir.canRead() || !dir.canWrite())
			return;
		
		int len = list.length;
		
		if(path.charAt(path.length() -1) != '/')
			_path = path + "/";
		else
			_path = path;
		
		try {
			ZipOutputStream zip_out = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(_path + name + ".zip"), BUFFER));
			
			for (int i = 0; i < len; i++)
				zip_folder(new File(_path + list[i]), zip_out);

			zip_out.close();
			
		} catch (FileNotFoundException e) {
			Log.e("File not found", e.getMessage());

		} catch (IOException e) {
			Log.e("IOException", e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param filePath
	 * @param newName
	 * @return
	 */
	public int renameTarget(String filePath, String newName) {
		File src = new File(filePath);
		String ext = "";
		File dest;
		
		if(src.isFile())
			/*get file extension*/
			ext = filePath.substring(filePath.lastIndexOf("."), filePath.length());
		
		if(newName.length() < 1)
			return -1;
	
		String temp = filePath.substring(0, filePath.lastIndexOf("/"));
		
		dest = new File(temp + "/" + newName + ext);
		if(src.renameTo(dest))
			return 0;
		else
			return -1;
	}
	
	/**
	 * 
	 * @param path
	 * @param name
	 * @return
	 */
	public int createDir(String path, String name) {
		if(path.length() < 1 || name.length() < 1)
			return -1;
		
		if (new File(path+name).mkdir())
			return 0;
		
		return -1;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public int deleteTarget(String path) {
		File target = new File(path);
		
		if(target.exists() && target.isFile() && target.canWrite()) {
			target.delete();
			return 0;
		}
		
		else if(target.exists() && target.isDirectory()) {
			String[] file_list = target.list();
			
			if(file_list.length == 0) {
				target.delete();
				return 0;
				
			} else {
				for(int i = 0; i < file_list.length; i++) {
					File temp_f = new File(target.getAbsolutePath() + "/" + file_list[i]);
					if(temp_f.isDirectory())
						deleteTarget(temp_f.getAbsolutePath());
					else if(temp_f.isFile())
						temp_f.delete();
				}
			}
			if(target.exists())
				if(target.delete())
					return 0;
		}	
		return -1;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean isDirectory(String name) {
		return new File(path_stack.peek() + "/" + name).isDirectory();
	}
		
	/**
	 * nice, make a good comment.
	 * 
	 * @param des
	 * @return
	 */
	public static String integerToIPAddress(int des) {
		String ip_address = "";
		int[] binary = new int[32];
		int[] value = {1,2,4,8,16,32,64,128};
		int i = 0, num = 0, count = 0;

	   	 while(des != 0) {
		    binary[i++] = des % 2;
		    des = des / 2;

		    if(count > 7){
				ip_address += num + ".";
			    count = 0;
				num = 0;
		    }

		    if(binary[i - 1] == 1)
		    	num += value[count];
		    count++;
		}
		ip_address += num;
		
		return ip_address;
	}
	
	/**
	 * 
	 * @param dir
	 * @param pathName
	 * @return
	 */
	public ArrayList<String> searchInDirectory(String dir, String pathName) {
		ArrayList<String> names = new ArrayList<String>();
		search_file(dir, pathName, names);

		return names;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public double getDirSize(String path) {
		if(dir_size != 0)
			dir_size = 0;
		
		get_dir_size(new File(path));
		
		return dir_size;
	}
	
	/*
	 * 
	 * @return
	 */
	private ArrayList<String> populate_list() {
		
		if(!dir_content.isEmpty())
			dir_content.clear();
		
		File file = new File(path_stack.peek());
		
		if(file.exists() && file.canRead()) {
			String[] list = file.list();
			int len = list.length;
						
			for (int i = 0; i < len; i++) {
				if(!show_hidden) {
					if(list[i].toString().charAt(0) != '.')
						dir_content.add(list[i]);
				} else {
					dir_content.add(list[i]);
				}
			}
				
		} else {
			dir_content.add("Emtpy");
		}
		
		return dir_content;
	}
	
	/*
	 * 
	 * @param file
	 * @param zout
	 * @throws IOException
	 */
	private void zip_folder(File file, ZipOutputStream zout) throws IOException {
		byte[] data = new byte[BUFFER];
		int read;
		
		if(file.isFile()){
			ZipEntry entry = new ZipEntry(file.getPath());
			zout.putNextEntry(entry);
			BufferedInputStream instream = new BufferedInputStream(new FileInputStream(file));
			
			while((read = instream.read(data, 0, BUFFER)) != -1)
				zout.write(data, 0, read);
			
			zout.closeEntry();
			instream.close();
			
		}else /*file is a directory*/{
			String[] list = file.list();
			int len = list.length;
			
			for(int i = 0; i < len; i++)
				zip_folder(new File(file.getPath() +"/"+ list[i]), zout);
		}
	
	}
	
	/*
	 * 
	 * @param path
	 */
	private void get_dir_size(File path) {
		File[] list = path.listFiles();
		int len = list.length;
		
		for (int i = 0; i < len; i++) {
			if(list[i].isFile())
				dir_size += list[i].length();
			
			else if(list[i].isDirectory() && list[i].canRead())
				get_dir_size(list[i]);
		}
	}

	/*
	 * 
	 * @param dir
	 * @param fileName
	 * @param n
	 */
	private void search_file(String dir, String fileName, ArrayList<String> n) {
		File root_dir = new File(dir);
		String[] list = root_dir.list();
		
		if(list != null) {
			int len = list.length;
			
			for (int i = 0; i < len; i++) {
				File check = new File(dir + "/" + list[i]);
				String name = check.getName();
					
				if(check.isFile() && name.toLowerCase().contains(fileName.toLowerCase())) {
					n.add(check.getPath());
				}
				else if(check.isDirectory()) {
					if(name.toLowerCase().contains(fileName.toLowerCase()))
						n.add(check.getPath());
					
					search_file(check.getAbsolutePath(), fileName, n);
				}
			}
		}
	}
}
