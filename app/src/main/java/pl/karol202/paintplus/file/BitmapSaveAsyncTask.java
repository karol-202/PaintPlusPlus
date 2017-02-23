package pl.karol202.paintplus.file;

import android.os.AsyncTask;

public class BitmapSaveAsyncTask extends AsyncTask<BitmapSaveParams, Void, Void>
{
	public interface OnBitmapSaveListener
	{
		void onBitmapSaved();
	}
	
	private OnBitmapSaveListener listener;
	
	@Override
	protected Void doInBackground(BitmapSaveParams... params)
	{
		listener = params[0].getListener();
		
		ImageLoader.saveBitmap(params[0].getBitmap(), params[0].getFilePath(), params[0].getQuality());
		return null;
	}
	
	@Override
	protected void onPostExecute(Void aVoid)
	{
		super.onPostExecute(aVoid);
		listener.onBitmapSaved();
	}
}