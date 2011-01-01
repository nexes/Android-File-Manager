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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.os.Handler;

import java.lang.ref.SoftReference;

public class ThumbnailCreator {
	private String mImage;
	private int mWidth;
	private int mHeight;

	public ThumbnailCreator(int width, int height) {
		mWidth = width;
		mHeight = height;
	}
	
	public void setBitmapToImageView(final String imageSrc, 
									 final Handler handle, 
									 final ImageView icon) {
		mImage = imageSrc;
		
		Thread thread = new Thread() {
			public void run() {
				synchronized (this) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					final SoftReference<Bitmap> image;

					options.inSampleSize = 32;
					image = new SoftReference<Bitmap>(BitmapFactory.decodeFile(mImage, options));
					
					handle.post(new Runnable() {
						public void run() {
							icon.setImageBitmap(image.get());
						}
					});
				}
			}
		};
		
		thread.start();
	}
}