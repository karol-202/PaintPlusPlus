package pl.karol202.paintplus.file;

import android.os.AsyncTask;

public class BitmapSaveAsyncTask extends AsyncTask<BitmapSaveParams, Void, Boolean>
{
	public interface OnBitmapSaveListener
	{
		void onBitmapSaved(boolean saved, String filePath);
	}
	
	private OnBitmapSaveListener listener;
	private String filePath;
	
	@Override
	protected Boolean doInBackground(BitmapSaveParams... params)
	{
		listener = params[0].getListener();
		filePath = params[0].getFilePath();
		
		return ImageLoader.saveBitmap(params[0].getBitmap(), filePath, params[0].getQuality());
	}
	
	@Override
	protected void onPostExecute(Boolean result)
	{
		super.onPostExecute(result);
		listener.onBitmapSaved(result, filePath);
	}
}