package pl.karol202.paintplus.options;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;
import pl.karol202.paintplus.AsyncBlocker;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.ActivityResultListener;
import pl.karol202.paintplus.file.ActivityFileSave;
import pl.karol202.paintplus.file.BitmapSaveAsyncTask;
import pl.karol202.paintplus.file.BitmapSaveParams;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.recent.OnFileEditListener;
import pl.karol202.paintplus.settings.ActivitySettings;

import static android.app.Activity.RESULT_OK;

public class OptionLayerSave extends Option implements ActivityResultListener, AsyncBlocker, BitmapSaveAsyncTask.OnBitmapSaveListener
{
	private static final int REQUEST_SAVE_LAYER = 4;
	
	private ActivityPaint activity;
	private OnFileEditListener listener;
	private AsyncManager asyncManager;
	private int quality;
	
	private AsyncTask asyncTask;
	
	public OptionLayerSave(ActivityPaint activity, Image image, AsyncManager asyncManager, OnFileEditListener listener)
	{
		super(activity, image);
		this.activity = activity;
		this.asyncManager = asyncManager;
		this.listener = listener;
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		this.quality = preferences.getInt(ActivitySettings.KEY_JPG_QUALITY, 100);
		
		this.activity.registerActivityResultListener(REQUEST_SAVE_LAYER, this);
	}
	
	@Override
	public void execute()
	{
		Intent intent = new Intent(context, ActivityFileSave.class);
		activity.startActivityForResult(intent, REQUEST_SAVE_LAYER);
	}
	
	@Override
	public void onActivityResult(int resultCode, Intent data)
	{
		activity.unregisterActivityResultListener(REQUEST_SAVE_LAYER);
		if(resultCode != RESULT_OK) return;
		String filePath = data.getStringExtra("filePath");
		
		saveBitmapAsynchronously(filePath);
	}
	
	private void saveBitmapAsynchronously(String filePath)
	{
		asyncManager.block(this);
		
		Layer layer = image.getSelectedLayer();
		BitmapSaveParams params = new BitmapSaveParams(this, layer.getBitmap(), filePath, quality);
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
	public void onBitmapSaved(boolean saved, String filePath, Bitmap bitmap)
	{
		asyncManager.unblock(this);
		if(!saved) Toast.makeText(activity, R.string.message_cannot_save_file, Toast.LENGTH_SHORT).show();
		else if(listener != null) listener.onFileEdited(filePath, bitmap);
	}
}