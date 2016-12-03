package pl.karol202.paintplus.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.util.GLHelper;

public class ImageLoader
{
	public static final String[] FORMATS = new String[] { "jpg", "jpeg" };
	
	public static void openImageFromFile(Image image, String path)
	{
		Bitmap photo = BitmapFactory.decodeFile(path);
		if(photo == null) return;
		
		float maxSize = GLHelper.getMaxTextureSize();
		if(photo.getWidth() < maxSize || photo.getHeight() < maxSize)
		{
			float widthRatio = photo.getWidth() / maxSize;
			float heightRatio = photo.getHeight() / maxSize;
			float higher = Math.max(widthRatio, heightRatio);
			int newWidth = (int) Math.floor(photo.getWidth() / higher);
			int newHeight = (int) Math.floor(photo.getHeight() / higher);
			Bitmap scaled = Bitmap.createScaledBitmap(photo, newWidth, newHeight, true);
			image.setBitmap(scaled);
		}
		else image.setBitmap(photo);
		image.centerView();
	}
	
	public static void saveImageToFile(Image image, String path)
	{
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!File saved " + path);
	}
}