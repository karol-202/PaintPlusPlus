/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package pl.karol202.paintplus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class AsyncManager implements DialogInterface.OnCancelListener
{
	private final int PROGRESS_BAR_DELAY = 50;
	
	private class ProgressDialogExecutor extends AsyncTask<Void, Void, Void>
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
			if(blocker != null) showProgressDialog(blocker.getMessage());
		}
	}
	
	private AsyncBlocker blocker;
	private Context context;
	
	private ProgressDialog dialog;
	
	public AsyncManager(Context context)
	{
		this.context = context;
	}
	
	public boolean block(AsyncBlocker newBlocker)
	{
		if(blocker != null) return false;
		
		blocker = newBlocker;
		new ProgressDialogExecutor().executeOnExecutor(THREAD_POOL_EXECUTOR);
		return true;
	}
	
	public boolean unblock(AsyncBlocker blockerToCheck)
	{
		if(blocker == null) return false;
		if(blockerToCheck != blocker) return false;
		
		blocker = null;
		hideProgressDialog();
		return true;
	}
	
	private void showProgressDialog(int messageId)
	{
		String title = context.getString(R.string.dialog_wait);
		String message = context.getString(messageId);
		dialog = ProgressDialog.show(context, title, message, true, true, this);
	}
	
	private void hideProgressDialog()
	{
		if(dialog != null) dialog.dismiss();
		dialog = null;
	}
	
	@Override
	public void onCancel(DialogInterface dialog)
	{
		blocker.cancel();
	}
	
	public boolean isBlocked()
	{
		return blocker != null;
	}
}