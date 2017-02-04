package pl.karol202.paintplus.options;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.ActivityPaint.ActivityResultListener;
import pl.karol202.paintplus.file.ActivityFileSave;
import pl.karol202.paintplus.file.ImageLoader;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.settings.ActivitySettings;

import static android.app.Activity.RESULT_OK;

public class OptionLayerSave extends Option implements ActivityResultListener
{
	private static final int REQUEST_SAVE_LAYER = 4;
	
	private ActivityPaint activity;
	private int quality;
	
	public OptionLayerSave(ActivityPaint activity, Image image)
	{
		super(activity, image);
		this.activity = activity;
		this.activity.registerActivityResultListener(REQUEST_SAVE_LAYER, this);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		this.quality = preferences.getInt(ActivitySettings.KEY_JPG_QUALITY, 100);
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
		
		Layer layer = image.getSelectedLayer();
		ImageLoader.saveBitmap(layer.getBitmap(), filePath, quality);
	}
}