package pl.karol202.paintplus.file;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;

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
		String filePath = params[0].getFilePath();
		Point bitmapSize = params[0].getBitmapSize();
		
		Bitmap bitmap = ImageLoader.openBitmapAndScale(filePath, bitmapSize);
		return new BitmapLoadResult(bitmap);
	}
	
	@Override
	protected void onPostExecute(BitmapLoadResult result)
	{
		super.onPostExecute(result);
		listener.onBitmapLoad(result.getBitmap());
	}
}