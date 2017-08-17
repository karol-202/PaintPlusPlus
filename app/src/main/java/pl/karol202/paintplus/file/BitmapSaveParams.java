package pl.karol202.paintplus.file;

import android.graphics.Bitmap;
import pl.karol202.paintplus.file.BitmapSaveAsyncTask.OnBitmapSaveListener;

public class BitmapSaveParams
{
	private OnBitmapSaveListener listener;
	private Bitmap bitmap;
	private String filePath;
	private int quality;
	
	public BitmapSaveParams(OnBitmapSaveListener listener, Bitmap bitmap, String filePath, int quality)
	{
		this.listener = listener;
		this.bitmap = bitmap;
		this.filePath = filePath;
		this.quality = quality;
	}
	
	OnBitmapSaveListener getListener()
	{
		return listener;
	}
	
	Bitmap getBitmap()
	{
		return bitmap;
	}
	
	String getFilePath()
	{
		return filePath;
	}
	
	int getQuality()
	{
		return quality;
	}
}