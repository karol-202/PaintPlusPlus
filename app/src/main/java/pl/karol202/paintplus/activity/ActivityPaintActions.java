package pl.karol202.paintplus.activity;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.options.*;

public class ActivityPaintActions
{
	private ActivityPaint activity;
	private Image image;
	private MenuInflater menuInflater;
	private PackageManager packageManager;
	
	ActivityPaintActions(ActivityPaint activity)
	{
		this.activity = activity;
		menuInflater = activity.getMenuInflater();
		packageManager = activity.getPackageManager();
	}
	
	public void inflateMenu(Menu menu)
	{
		menuInflater.inflate(R.menu.menu_paint, menu);
		image = activity.getImage();
	}
	
	public void prepareMenu(Menu menu)
	{
		boolean anyDrawerOpen = activity.isAnyDrawerOpen();
		setItemsVisibility(menu, !anyDrawerOpen);
		
		preparePhotoCaptureOption(menu);
		prepareFileOpenOption(menu);
		prepareFileSaveOption(menu);
	}
	
	private void setItemsVisibility(Menu menu, boolean visible)
	{
		for(int i = 0; i < menu.size(); i++)
			menu.getItem(i).setVisible(visible);
	}
	
	private void preparePhotoCaptureOption(Menu menu)
	{
		boolean hasCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
		menu.findItem(R.id.action_capture_photo).setEnabled(hasCamera);
	}
	
	private void prepareFileOpenOption(Menu menu)
	{
		String state = Environment.getExternalStorageState();
		boolean enable = state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
		menu.findItem(R.id.action_open_image).setEnabled(enable);
	}
	
	private void prepareFileSaveOption(Menu menu)
	{
		String state = Environment.getExternalStorageState();
		boolean enable = state.equals(Environment.MEDIA_MOUNTED);
		menu.findItem(R.id.action_save_image).setEnabled(enable);
	}
	
	public boolean handleAction(MenuItem item)
	{
		int id = item.getItemId();
		switch(id)
		{
		case R.id.action_layers:
			activity.toggleLayersSheet();
			return true;
		case R.id.action_tool:
			activity.togglePropertiesDrawer();
			return true;
		
		case R.id.action_new_image:
			new OptionFileNew(activity, image).execute();
			return true;
		case R.id.action_capture_photo:
			new OptionFileCapturePhoto(activity, image).execute();
			return true;
		case R.id.action_open_image:
			new OptionFileOpen(activity, image).execute();
			return true;
		case R.id.action_save_image:
			new OptionFileSave(activity, image).execute();
			return true;
		
		case R.id.action_resize_image:
			new OptionImageResize(activity, image).execute();
			return true;
		case R.id.action_scale_image:
			new OptionImageScale(activity, image).execute();
			return true;
		case R.id.action_flip_image:
			new OptionImageFlip(activity, image).execute();
			return true;
		
		case R.id.action_new_layer:
			new OptionLayerNew(activity, image).execute();
			return true;
		case R.id.action_resize_layer:
			new OptionLayerResize(activity, image).execute();
			return true;
		case R.id.action_scale_layer:
			new OptionLayerScale(activity, image).execute();
			return true;
		case R.id.action_flip_layer:
			new OptionLayerFlip(activity, image).execute();
			return true;
		case R.id.action_rotate_layer:
			new OptionLayerRotate(activity, image).execute();
			return true;
			
		case R.id.action_settings:
			activity.showSettingsActivity();
			return true;
		}
		return false;
	}
}