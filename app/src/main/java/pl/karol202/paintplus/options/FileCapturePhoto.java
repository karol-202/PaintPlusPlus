package pl.karol202.paintplus.options;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.activity.ActivityPaint;

import static android.app.Activity.RESULT_OK;

public class FileCapturePhoto extends Option implements ActivityPaint.ActivityResultListener
{
	private static final int REQUEST_CAPTURE_PHOTO = 0;
	
	private ActivityPaint activity;
	
	public FileCapturePhoto(ActivityPaint activity, Image image)
	{
		super(activity, image);
		this.activity = activity;
		this.activity.registerActivityResultListener(REQUEST_CAPTURE_PHOTO, this);
	}
	
	@Override
	public void execute()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(intent.resolveActivity(activity.getPackageManager()) != null)
			activity.startActivityForResult(intent, REQUEST_CAPTURE_PHOTO);
	}
	
	@Override
	public void onActivityResult(int resultCode, Intent data)
	{
		if(resultCode != RESULT_OK) return;
		Bundle extras = data.getExtras();
		Bitmap photo = (Bitmap) extras.get("data");
		image.setBitmap(photo);
		image.centerView();
	}
}