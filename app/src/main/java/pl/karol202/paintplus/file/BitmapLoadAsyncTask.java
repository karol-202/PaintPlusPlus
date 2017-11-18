package pl.karol202.paintplus.file;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;

import java.io.FileDescriptor;

class BitmapLoadAsyncTask extends AsyncTask<BitmapLoadParams, Void, BitmapLoadResult>
{
	interface OnBitmapLoadListener
	{
		void onBitmapLoad(Bitmap bitmap);
	}
	
	private OnBitmapLoadListener listener;
	
	@Override
	protected BitmapLoadResult doInBackground(BitmapLoadParams... params)
	{
		listener = params[0].getListener();
		FileDescriptor fileDescriptor = params[0].getFileDescriptor();
		Point bitmapSize = params[0].getBitmapSize();
		
		Bitmap bitmap = ImageLoader.openBitmapAndScale(fileDescriptor, bitmapSize);
		return new BitmapLoadResult(bitmap);
	}
	
	@Override
	protected void onPostExecute(BitmapLoadResult result)
	{
		super.onPostExecute(result);
		listener.onBitmapLoad(result.getBitmap());
	}
}