package pl.karol202.paintplus.file;

import android.graphics.Bitmap;

class BitmapLoadResult
{
	private Bitmap bitmap;
	
	BitmapLoadResult(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}
	
	public Bitmap getBitmap()
	{
		return bitmap;
	}
}