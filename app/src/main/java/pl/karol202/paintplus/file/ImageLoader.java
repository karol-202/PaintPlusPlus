package pl.karol202.paintplus.file;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import com.google.firebase.crash.FirebaseCrash;
import pl.karol202.paintplus.util.GraphicsHelper;

import java.io.File;
import java.io.FileOutputStream;

public class ImageLoader
{
	static final String[] OPEN_FORMATS = new String[] { "jpg", "jpeg", "png", "webp", "bmp", "gif" };
	static final String[] SAVE_FORMATS = new String[] { "jpg", "jpeg", "png", "webp" };
	
	public static Bitmap openBitmapAndScaleIfNecessary(String path)
	{
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		if(bitmap == null) return null;
		
		Point bitmapSize = new Point(bitmap.getWidth(), bitmap.getHeight());
		if(isBitmapTooBig(bitmapSize))
		{
			bitmapSize = scaleBitmapSizeIfNecessary(bitmapSize);
			return Bitmap.createScaledBitmap(bitmap, bitmapSize.x, bitmapSize.y, true);
		}
		else return bitmap;
	}
	
	static Bitmap openBitmapAndScale(String path, Point targetSize)
	{
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		if(bitmap == null) return null;
		
		return Bitmap.createScaledBitmap(bitmap, targetSize.x, targetSize.y, true);
	}
	
	static boolean isBitmapTooBig(Point size)
	{
		float maxSize = GraphicsHelper.getMaxTextureSize();
		return size.x > maxSize || size.y > maxSize;
	}
	
	static Point getBitmapSize(String path)
	{
		Options options = new Options();
		options.inJustDecodeBounds = true;
		
		BitmapFactory.decodeFile(path, options);
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
	
	public static boolean saveBitmap(Bitmap bitmap, String path, int quality)
	{
		try
		{
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			CompressFormat format = getExtension(path);
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
	
	private static CompressFormat getExtension(String path)
	{
		String[] parts = path.split("\\.");
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