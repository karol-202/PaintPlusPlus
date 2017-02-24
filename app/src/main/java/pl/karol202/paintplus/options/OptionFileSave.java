package pl.karol202.paintplus.options;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import pl.karol202.paintplus.AsyncBlocker;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.ActivityPaint.ActivityResultListener;
import pl.karol202.paintplus.file.ActivityFileSave;
import pl.karol202.paintplus.file.BitmapSaveAsyncTask;
import pl.karol202.paintplus.file.BitmapSaveAsyncTask.OnBitmapSaveListener;
import pl.karol202.paintplus.file.BitmapSaveParams;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.settings.ActivitySettings;

import static android.app.Activity.RESULT_OK;

public class OptionFileSave extends Option implements ActivityResultListener, AsyncBlocker, OnBitmapSaveListener
{
	private static final int REQUEST_SAVE_FILE = 2;
	
	private ActivityPaint activity;
	private AsyncManager asyncManager;
	private int quality;
	
	private AsyncTask asyncTask;
	
	public OptionFileSave(ActivityPaint activity, Image image, AsyncManager asyncManager)
	{
		super(activity, image);
		this.activity = activity;
		this.activity.registerActivityResultListener(REQUEST_SAVE_FILE, this);
		this.asyncManager = asyncManager;
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		this.quality = preferences.getInt(ActivitySettings.KEY_JPG_QUALITY, 100);
	}
	
	@Override
	public void execute()
	{
		Intent intent = new Intent(context, ActivityFileSave.class);
		activity.startActivityForResult(intent, REQUEST_SAVE_FILE);
	}
	
	@Override
	public void onActivityResult(int resultCode, Intent data)
	{
		activity.unregisterActivityResultListener(REQUEST_SAVE_FILE);
		if(resultCode != RESULT_OK) return;
		String filePath = data.getStringExtra("filePath");
		saveBitmapAsynchronously(filePath);
	}
	
	private void saveBitmapAsynchronously(String filePath)
	{
		asyncManager.block(this);
		
		BitmapSaveParams params = new BitmapSaveParams(this, image.getFullImage(), filePath, quality);
		asyncTask = new BitmapSaveAsyncTask().execute(params);
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
	public void onBitmapSaved()
	{
		asyncManager.unblock(this);
	}
}