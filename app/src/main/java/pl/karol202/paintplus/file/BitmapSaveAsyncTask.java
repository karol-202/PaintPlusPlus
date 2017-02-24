package pl.karol202.paintplus.file;

import android.os.AsyncTask;

public class BitmapSaveAsyncTask extends AsyncTask<BitmapSaveParams, Void, Boolean>
{
	public interface OnBitmapSaveListener
	{
		void onBitmapSaved(boolean saved);
	}
	
	private OnBitmapSaveListener listener;
	
	@Override
	protected Boolean doInBackground(BitmapSaveParams... params)
	{
		listener = params[0].getListener();
		
		return ImageLoader.saveBitmap(params[0].getBitmap(), params[0].getFilePath(), params[0].getQuality());
	}
	
	@Override
	protected void onPostExecute(Boolean result)
	{
		super.onPostExecute(result);
		listener.onBitmapSaved(result);
	}
}