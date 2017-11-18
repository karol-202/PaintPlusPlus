/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package pl.karol202.paintplus.file;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import com.google.firebase.crash.FirebaseCrash;
import pl.karol202.paintplus.util.GraphicsHelper;

import java.io.FileDescriptor;
import java.io.FileOutputStream;

public class ImageLoader
{
	public static final String[] OPEN_FORMATS = new String[] { "jpg", "jpeg", "png", "webp", "bmp", "gif" };
	public static final String[] SAVE_FORMATS = new String[] { "jpg", "jpeg", "png", "webp" };
	
	public static Bitmap openBitmapAndScaleIfNecessary(FileDescriptor fileDescriptor)
	{
		Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
		if(bitmap == null) return null;
		
		Point bitmapSize = new Point(bitmap.getWidth(), bitmap.getHeight());
		if(isBitmapTooBig(bitmapSize))
		{
			bitmapSize = scaleBitmapSizeIfNecessary(bitmapSize);
			return Bitmap.createScaledBitmap(bitmap, bitmapSize.x, bitmapSize.y, true);
		}
		else return bitmap;
	}
	
	static Bitmap openBitmapAndScale(FileDescriptor fileDescriptor, Point targetSize)
	{
		Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
		if(bitmap == null) return null;
		
		return Bitmap.createScaledBitmap(bitmap, targetSize.x, targetSize.y, true);
	}
	
	static boolean isBitmapTooBig(Point size)
	{
		float maxSize = GraphicsHelper.getMaxTextureSize();
		return size.x > maxSize || size.y > maxSize;
	}
	
	static Point getBitmapSize(FileDescriptor fileDescriptor)
	{
		Options options = new Options();
		options.inJustDecodeBounds = true;
		
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
		return new Point(options.outWidth,options.outHeight);
	}
	
	static Point scaleBitmapSizeIfNecessary(Point originalSize)
	{
		if(isBitmapTooBig(originalSize))
		{
			float maxSize = GraphicsHelper.getMaxTextureSize();
			float widthRatio = originalSize.x / maxSize;
			float heightRatio = originalSize.y / maxSize;
			float higher = Math.max(widthRatio, heightRatio);
			int newWidth = (int) Math.floor(originalSize.x / higher);
			int newHeight = (int) Math.floor(originalSize.y / higher);
			return new Point(newWidth, newHeight);
		}
		else return originalSize;
	}
	
	public static boolean saveBitmap(Bitmap bitmap, FileDescriptor fileDescriptor, String name, int quality)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(fileDescriptor);
			CompressFormat format = getExtension(name);
			bitmap.compress(format, quality, fos);
			fos.close();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			FirebaseCrash.report(new RuntimeException("Cannot save bitmap to file.", e));
			return false;
		}
	}
	
	private static CompressFormat getExtension(String name)
	{
		String[] parts = name.split("\\.");
		String extension = parts[parts.length - 1].toLowerCase();
		switch(extension)
		{
		case "jpg":
		case "jpeg": return CompressFormat.JPEG;
		case "png": return CompressFormat.PNG;
		case "webp": return CompressFormat.WEBP;
		default:
			Exception e =  new IllegalArgumentException("Unsupported format: " + extension);
			e.printStackTrace();
			FirebaseCrash.report(e);
			return null;
		}
	}
}