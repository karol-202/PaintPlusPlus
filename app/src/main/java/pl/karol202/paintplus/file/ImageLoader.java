package pl.karol202.paintplus.file;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import com.google.firebase.crash.FirebaseCrash;
import pl.karol202.paintplus.util.GraphicsHelper;

import java.io.File;
import java.io.FileOutputStream;

public class ImageLoader
{
	public static final String[] OPEN_FORMATS = new String[] { "jpg", "jpeg", "png", "webp", "bmp", "gif" };
	public static final String[] SAVE_FORMATS = new String[] { "jpg", "jpeg", "png", "webp" };
	
	public static Bitmap openBitmap(String path)
	{
		Bitmap photo = BitmapFactory.decodeFile(path);
		if(photo == null) return null;
		
		float maxSize = GraphicsHelper.getMaxTextureSize();
		if(photo.getWidth() > maxSize || photo.getHeight() > maxSize)
		{
			float widthRatio = photo.getWidth() / maxSize;
			float heightRatio = photo.getHeight() / maxSize;
			float higher = Math.max(widthRatio, heightRatio);
			int newWidth = (int) Math.floor(photo.getWidth() / higher);
			int newHeight = (int) Math.floor(photo.getHeight() / higher);
			Bitmap scaled = Bitmap.createScaledBitmap(photo, newWidth, newHeight, true);
			return scaled;
		}
		else return photo;
		
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