package pl.karol202.paintplus.file;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.util.GLHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageLoader
{
	public static final String[] OPEN_FORMATS = new String[] { "jpg", "jpeg", "png", "webp", "bmp", "gif" };
	public static final String[] SAVE_FORMATS = new String[] { "jpg", "jpeg", "png", "webp" };
	
	public static Bitmap openBitmap(String path)
	{
		Bitmap photo = BitmapFactory.decodeFile(path);
		if(photo == null) return null;
		
		float maxSize = GLHelper.getMaxTextureSize();
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
	
	public static void saveImageToFile(Image image, String path, int quality)
	{
		try
		{
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			CompressFormat format = getExtension(path);
			Bitmap bitmap = image.getFullImage();
			bitmap.compress(format, quality, fos);
			fos.close();
		}
		catch(IOException e)
		{
			throw new RuntimeException("Cannot save image to file.", e);
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
		default: throw new RuntimeException("Unsupported format: " + extension);
		}
	}
}