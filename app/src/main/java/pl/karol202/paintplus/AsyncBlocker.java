package pl.karol202.paintplus;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AsyncBlocker
{
	private final int PROGRESS_BAR_DELAY = 50;
	
	private class ProgressBarToggle extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params)
		{
			try
			{
				Thread.sleep(PROGRESS_BAR_DELAY);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			progressBar.setVisibility(blocker != null ? VISIBLE : GONE);
		}
	}
	
	private Object blocker;
	
	private ProgressBar progressBar;
	
	public AsyncBlocker(ProgressBar progressBar)
	{
		this.progressBar = progressBar;
	}
	
	public boolean block(Object newBlocker)
	{
		if(blocker != null) return false;
		
		blocker = newBlocker;
		new ProgressBarToggle().executeOnExecutor(THREAD_POOL_EXECUTOR);
		return true;
	}
	
	public boolean unblock(Object blockerToCheck)
	{
		if(blocker == null) return false;
		if(blockerToCheck != blocker) return false;
		
		blocker = null;
		progressBar.setVisibility(GONE);
		return true;
	}
	
	public boolean isBlocked()
	{
		return blocker != null;
	}
}