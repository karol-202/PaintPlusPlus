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
	
	public OnBitmapSaveListener getListener()
	{
		return listener;
	}
	
	public Bitmap getBitmap()
	{
		return bitmap;
	}
	
	public String getFilePath()
	{
		return filePath;
	}
	
	public int getQuality()
	{
		return quality;
	}
}