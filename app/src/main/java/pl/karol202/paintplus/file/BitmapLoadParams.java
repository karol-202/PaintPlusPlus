package pl.karol202.paintplus.file;

import android.graphics.Point;
import pl.karol202.paintplus.file.BitmapLoadAsyncTask.OnBitmapLoadListener;

class BitmapLoadParams
{
	private OnBitmapLoadListener listener;
	private String filePath;
	private Point bitmapSize;
	
	BitmapLoadParams(OnBitmapLoadListener listener, String filePath, Point bitmapSize)
	{
		this.listener = listener;
		this.filePath = filePath;
		this.bitmapSize = bitmapSize;
	}
	
	OnBitmapLoadListener getListener()
	{
		return listener;
	}
	
	String getFilePath()
	{
		return filePath;
	}
	
	Point getBitmapSize()
	{
		return bitmapSize;
	}
}