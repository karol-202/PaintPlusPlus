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

package pl.karol202.paintplus.options;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.widget.Toast;
import pl.karol202.paintplus.AsyncBlocker;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.ActivityResultListener;
import pl.karol202.paintplus.file.*;
import pl.karol202.paintplus.file.BitmapSaveAsyncTask.OnBitmapSaveListener;
import pl.karol202.paintplus.file.explorer.FileExplorer;
import pl.karol202.paintplus.file.explorer.FileExplorerFactory;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.recent.OnFileEditListener;
import pl.karol202.paintplus.settings.ActivitySettings;

import static android.app.Activity.RESULT_OK;

public class OptionFileSave extends Option implements ActivityResultListener, AsyncBlocker, OnBitmapSaveListener
{
	private static final int REQUEST_SAVE_FILE = 2;
	
	private ActivityPaint activity;
	private OnFileEditListener listener;
	private AsyncManager asyncManager;
	private int quality;
	
	private BitmapSaveAsyncTask asyncTask;
	private Uri uri;
	private ParcelFileDescriptor parcelFileDescriptor;
	
	public OptionFileSave(ActivityPaint activity, Image image, AsyncManager asyncManager, OnFileEditListener listener)
	{
		super(activity, image);
		this.activity = activity;
		this.asyncManager = asyncManager;
		this.listener = listener;
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		this.quality = preferences.getInt(ActivitySettings.KEY_JPG_QUALITY, 100);
	}
	
	@Override
	public void execute()
	{
		activity.registerActivityResultListener(REQUEST_SAVE_FILE, this);
		
		FileExplorer explorer = FileExplorerFactory.createFileExplorer(activity);
		explorer.saveFile(REQUEST_SAVE_FILE);
	}
	
	public void execute(Uri uri)
	{
		this.uri = uri;
		saveBitmapAsynchronously();
	}
	
	@Override
	public void onActivityResult(int resultCode, Intent intent)
	{
		activity.unregisterActivityResultListener(REQUEST_SAVE_FILE);
		if(resultCode != RESULT_OK) return;
		uri = intent.getData();
		saveBitmapAsynchronously();
	}
	
	private void saveBitmapAsynchronously()
	{
		image.setLastUri(uri);
		asyncManager.block(this);
		
		parcelFileDescriptor = UriUtils.createFileSaveDescriptor(context, uri);
		UriMetadata metadata = new UriMetadata(context, uri);
		BitmapSaveParams params = new BitmapSaveParams(this, image.getFullImage(), metadata.getDisplayName(),
													   parcelFileDescriptor.getFileDescriptor(), quality);
		asyncTask = new BitmapSaveAsyncTask();
		asyncTask.execute(params);
	}
	
	@Override
	public void cancel()
	{
		asyncTask.cancel(true);
		asyncManager.unblock(this);
	}
	
	@Override
	public int getMessage()
	{
		return R.string.dialog_save_message;
	}
	
	@Override
	public void onBitmapSaved(BitmapSaveResult result)
	{
		asyncManager.unblock(this);
		UriUtils.closeFileDescriptor(parcelFileDescriptor);
		
		switch(result.getResult())
		{
		case SUCCESSFUL:
			if(listener != null) listener.onFileEdited(uri, result.getBitmap());
			break;
		case ERROR:
			UriUtils.deleteDocument(context, uri);
			Toast.makeText(activity, R.string.message_cannot_save_file, Toast.LENGTH_SHORT).show();
			break;
		case UNSUPPORTED_FORMAT:
			UriUtils.deleteDocument(context, uri);
			Toast.makeText(activity, R.string.message_unsupported_format, Toast.LENGTH_SHORT).show();
			break;
		}
	}
}