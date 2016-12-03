package pl.karol202.paintplus.options;

import android.content.Intent;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.activity.ActivityFileSave;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.file.ImageLoader;

import static android.app.Activity.RESULT_OK;

public class OptionFileSave extends Option implements ActivityPaint.ActivityResultListener
{
	private static final int REQUEST_SAVE_FILE = 2;
	
	private ActivityPaint activity;
	
	public OptionFileSave(ActivityPaint activity, Image image)
	{
		super(activity, image);
		this.activity = activity;
		activity.registerActivityResultListener(REQUEST_SAVE_FILE, this);
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
		
		ImageLoader.saveImageToFile(image, filePath);
	}
}