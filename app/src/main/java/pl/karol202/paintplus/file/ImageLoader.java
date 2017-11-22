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
import com.ultrasonic.android.image.bitmap.util.AndroidBmpUtil;
import com.waynejo.androidndkgif.GifEncoder;
import pl.karol202.paintplus.ErrorHandler;
import pl.karol202.paintplus.file.BitmapSaveFormat.GIFSaveFormat;
import pl.karol202.paintplus.file.BitmapSaveFormat.JPEGSaveFormat;
import pl.karol202.paintplus.file.BitmapSaveFormat.PNGSaveFormat;
import pl.karol202.paintplus.util.GraphicsHelper;

import java.io.*;

import static pl.karol202.paintplus.file.BitmapSaveFormat.*;

public class ImageLoader
{
	public static final String[] OPEN_FORMATS = new String[] { "jpg", "jpeg", "png", "webp", "bmp", "gif" };
	public static final String[] SAVE_FORMATS = new String[] { "jpg", "jpeg", "png", "webp", "bmp", "gif" };
	
	private static File temporaryGifFile;
	
	public static void setTemporaryFileLocation(File location)
	{
		ImageLoader.temporaryGifFile = new File(location, "tmp.gif");
	}
	
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
	
	public static BitmapSaveFormat getFormat(String name)
	{
		String[] parts = name.split("\\.");
		String extension = parts[parts.length - 1].toLowerCase();
		switch(extension)
		{
		case "jpg":
		case "jpeg": return new JPEGSaveFormat();
		case "png": return new PNGSaveFormat();
		case "webp": return new WEBPSaveFormat();
		case "bmp": return new BMPSaveFormat();
		case "gif": return new GIFSaveFormat();
		default: return null;
		}
	}
	
	public static BitmapSaveResult.Result saveBitmap(Bitmap bitmap, FileDescriptor fileDescriptor, BitmapSaveFormat format)
	{
		FileOutputStream fos = null;
		
		try
		{
			fos = new FileOutputStream(fileDescriptor);
			if(!compressBitmap(bitmap, fos, format)) throw new RuntimeException("Cannot compress bitmap.");
		}
		catch(Exception e)
		{
			ErrorHandler.report(e);
			return BitmapSaveResult.Result.ERROR;
		}
		finally
		{
			try
			{
				if(fos != null) fos.close();
			}
			catch(IOException e)
			{
				ErrorHandler.report(e);
			}
		}
		
		return BitmapSaveResult.Result.SUCCESSFUL;
	}
	
	private static boolean compressBitmap(Bitmap bitmap, FileOutputStream outputStream, BitmapSaveFormat format)
	{
		if(format instanceof JPEGSaveFormat)
			return bitmap.compress(CompressFormat.JPEG, ((JPEGSaveFormat) format).getQuality(), outputStream);
		else if(format instanceof PNGSaveFormat) return bitmap.compress(CompressFormat.PNG, 100, outputStream);
		else if(format instanceof WEBPSaveFormat) return bitmap.compress(CompressFormat.WEBP, 100, outputStream);
		else if(format instanceof BMPSaveFormat) return compressToBmp(bitmap, outputStream);
		else if(format instanceof GIFSaveFormat) return tryToCompressToGif(bitmap, outputStream, (GIFSaveFormat) format);
		return false;
	}
	
	private static boolean compressToBmp(Bitmap bitmap, FileOutputStream outputStream)
	{
		AndroidBmpUtil util = new AndroidBmpUtil();
		return util.save(bitmap, outputStream);
	}
	
	private static boolean tryToCompressToGif(Bitmap bitmap, FileOutputStream outputStream, GIFSaveFormat format)
	{
		try
		{
			return compressToGif(bitmap, outputStream, format);
		}
		catch(IOException e)
		{
			ErrorHandler.report(e);
			return false;
		}
	}
	
	private static boolean compressToGif(Bitmap bitmap, FileOutputStream outputStream, GIFSaveFormat format) throws IOException
	{
		boolean result = compressToGif(bitmap, temporaryGifFile.getAbsolutePath(), format);
		
		FileInputStream inputStream = new FileInputStream(temporaryGifFile);
		if(result) copyStream(inputStream, outputStream);
		inputStream.close();
		
		temporaryGifFile.delete();
		return result;
	}
	
	private static boolean compressToGif(Bitmap bitmap, String path, GIFSaveFormat format) throws FileNotFoundException
	{
		boolean result;
		GifEncoder encoder = new GifEncoder();
		encoder.setDither(format.getDithering());
		encoder.init(bitmap.getWidth(), bitmap.getHeight(), path, GifEncoder.EncodingType.ENCODING_TYPE_FAST);
		result = encoder.encodeFrame(bitmap, 0);
		encoder.close();
		return result;
	}
	
	private static void copyStream(InputStream is, OutputStream os) throws IOException
	{
		byte[] buffer = new byte[1024];
		int length;
		while((length = is.read(buffer)) > 0)
			os.write(buffer, 0, length);
	}
}