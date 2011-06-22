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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.io.File;

public class ThumbnailCreator {	
	private int mWidth;
	private int mHeight;
	private SoftReference<Bitmap> mThumb;
	private static HashMap<String, Bitmap> mCacheMap = null;

	public ThumbnailCreator(int width, int height) {
		mHeight = height;
		mWidth = width;
		
		if(mCacheMap == null)
			mCacheMap = new HashMap<String, Bitmap>();
	}
	
	public Bitmap isBitmapCached(String name) {
		return mCacheMap.get(name);
	}

	/*
	 * Absolutely maddening!!!
	 */
	public void createNewThumbnail(final String imageName, final Handler handler) {
		
		Thread thread = new Thread() {
			public void run() {
				File file = new File(imageName);
				
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.outWidth = mWidth;
				options.outHeight = mHeight;
				
				if (file.length() > 200000 && file.length() < 1500000) {
					options.inSampleSize = 16;
					mThumb = new SoftReference<Bitmap>(BitmapFactory.decodeFile(imageName, options));
					
				} else if (file.length() > 1500000) {
					options.inSampleSize = 64;
					options.outWidth = 32;
					options.outHeight = 32;
					mThumb = new SoftReference<Bitmap>(BitmapFactory.decodeFile(imageName, options));
				
				} else if (file.length() < 200000) {
					mThumb = new SoftReference<Bitmap>(Bitmap.createScaledBitmap(
							 						   BitmapFactory.decodeFile(imageName),
							 						   mWidth,
							 						   mHeight,
							 						   false));
				}

				mCacheMap.put(imageName, mThumb.get());
				handler.post(new Runnable() {
					@Override
					public void run() {
						Message msg = handler.obtainMessage();
						msg.obj = (Bitmap)mThumb.get();
						msg.sendToTarget();
					}
				});
			}
		};
		thread.start();
	}
}