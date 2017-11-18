package pl.karol202.paintplus.file;

import android.graphics.Point;
import pl.karol202.paintplus.file.BitmapLoadAsyncTask.OnBitmapLoadListener;

import java.io.FileDescriptor;

class BitmapLoadParams
{
	private OnBitmapLoadListener listener;
	private FileDescriptor fileDescriptor;
	private Point bitmapSize;
	
	BitmapLoadParams(OnBitmapLoadListener listener, FileDescriptor fileDescriptor, Point bitmapSize)
	{
		this.listener = listener;
		this.fileDescriptor = fileDescriptor;
		this.bitmapSize = bitmapSize;
	}
	
	OnBitmapLoadListener getListener()
	{
		return listener;
	}
	
	FileDescriptor getFileDescriptor()
	{
		return fileDescriptor;
	}
	
	Point getBitmapSize()
	{
		return bitmapSize;
	}
}