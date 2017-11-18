package pl.karol202.paintplus.file;

import android.graphics.Bitmap;
import pl.karol202.paintplus.file.BitmapSaveAsyncTask.OnBitmapSaveListener;

import java.io.FileDescriptor;

public class BitmapSaveParams
{
	private OnBitmapSaveListener listener;
	private Bitmap bitmap;
	private String name;
	private FileDescriptor fileDescriptor;
	private int quality;
	
	public BitmapSaveParams(OnBitmapSaveListener listener, Bitmap bitmap, String name, FileDescriptor fileDescriptor, int quality)
	{
		this.listener = listener;
		this.bitmap = bitmap;
		this.name = name;
		this.fileDescriptor = fileDescriptor;
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
	
	String getName()
	{
		return name;
	}
	
	FileDescriptor getFileDescriptor()
	{
		return fileDescriptor;
	}
	
	int getQuality()
	{
		return quality;
	}
}