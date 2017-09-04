package pl.karol202.paintplus.file;

import android.graphics.Bitmap;

class BitmapSaveResult
{
	private Bitmap bitmap;
	private boolean saved;
	
	BitmapSaveResult(Bitmap bitmap, boolean saved)
	{
		this.bitmap = bitmap;
		this.saved = saved;
	}
	
	Bitmap getBitmap()
	{
		return bitmap;
	}
	
	boolean isSaved()
	{
		return saved;
	}
}