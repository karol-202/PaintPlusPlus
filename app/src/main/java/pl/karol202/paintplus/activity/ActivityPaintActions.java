package pl.karol202.paintplus.activity;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorChannel.ColorChannelType;
import pl.karol202.paintplus.image.Clipboard;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.options.*;
import pl.karol202.paintplus.tool.selection.Selection;
import pl.karol202.paintplus.tool.selection.Selection.OnSelectionChangeListener;

class ActivityPaintActions
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
	
	void inflateMenu(Menu menu)
	{
		menuInflater.inflate(R.menu.menu_paint, menu);
		image = activity.getImage();
		image.addOnSelectionChangeListener(new OnSelectionChangeListener()
		{
			@Override
			public void onSelectionChanged()
			{
				activity.invalidateOptionsMenu();
			}
		});
	}
	
	void prepareMenu(Menu menu)
	{
		boolean anyDrawerOpen = activity.isAnyDrawerOpen();
		setItemsVisibility(menu, !anyDrawerOpen);
		
		preparePhotoCaptureOption(menu);
		prepareFileOpenOption(menu);
		prepareFileSaveOption(menu);
		prepareClipboardOptions(menu);
		prepareSnapOptions(menu);
	}
	
	private void setItemsVisibility(Menu menu, boolean visible)
	{
		for(int i = 0; i < menu.size(); i++) menu.getItem(i).setVisible(visible);
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
		menu.findItem(R.id.action_open_layer).setEnabled(enable);
	}
	
	private void prepareFileSaveOption(Menu menu)
	{
		String state = Environment.getExternalStorageState();
		boolean savingAs = state.equals(Environment.MEDIA_MOUNTED);
		menu.findItem(R.id.action_save_image_as).setEnabled(savingAs);
		menu.findItem(R.id.action_save_layer).setEnabled(savingAs);
		
		boolean knownPath = image.getLastPath() != null;
		menu.findItem(R.id.action_save_image).setEnabled(savingAs && knownPath);
	}
	
	private void prepareClipboardOptions(Menu menu)
	{
		Selection selection = image.getSelection();
		menu.findItem(R.id.action_cut).setEnabled(!selection.isEmpty());
		menu.findItem(R.id.action_copy).setEnabled(!selection.isEmpty());
		
		Clipboard clipboard = image.getClipboard();
		menu.findItem(R.id.action_paste).setEnabled(!clipboard.isEmpty() || image.getLayersAmount() < Image.MAX_LAYERS);
	}
	
	private void prepareSnapOptions(Menu menu)
	{
		boolean grid = menu.findItem(R.id.action_grid).isChecked();
		MenuItem snapToGrid = menu.findItem(R.id.action_snap_to_grid);
		snapToGrid.setEnabled(grid);
		if(!grid) snapToGrid.setChecked(false);
	}
	
	boolean handleAction(MenuItem item)
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
			new OptionFileOpen(activity, image, activity.getFileEditListener()).execute();
			return true;
		case R.id.action_save_image:
			String path = image.getLastPath();
			new OptionFileSave(activity, image, activity.getAsyncManager(), activity.getFileEditListener()).execute(path);
			return true;
		case R.id.action_save_image_as:
			new OptionFileSave(activity, image, activity.getAsyncManager(), activity.getFileEditListener()).execute();
			return true;
		
		case R.id.action_cut:
			image.cut();
			activity.invalidateOptionsMenu();
			return true;
		case R.id.action_copy:
			image.copy();
			activity.invalidateOptionsMenu();
			return true;
		case R.id.action_paste:
			image.paste();
			return true;
			
		case R.id.action_set_zoom:
			new OptionSetZoom(activity, image).execute();
			return true;
		case R.id.action_zoom_default:
			image.setZoom(1f);
			return true;
		case R.id.action_image_center:
			image.centerView();
			return true;
		case R.id.action_grid:
			item.setChecked(!item.isChecked());
			activity.getPaintView().setGridEnabled(item.isChecked());
			return true;
		case R.id.action_snap_to_grid:
			item.setChecked(!item.isChecked());
			activity.getPaintView().setSnapToGrid(item.isChecked());
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
		case R.id.action_open_layer:
			new OptionLayerOpen(activity, image).execute();
			return true;
		case R.id.action_save_layer:
			new OptionLayerSave(activity, image, activity.getAsyncManager(), activity.getFileEditListener()).execute();
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
			
		case R.id.action_select_all:
			image.selectAll();
			return true;
		case R.id.action_select_nothing:
			image.selectNothing();
			return true;
		case R.id.action_revert_selection:
			image.revertSelection();
			return true;
			
		case R.id.action_colors_invert:
			new OptionColorsInvert(activity, image).execute();
			return true;
		case R.id.action_colors_brightness:
			new OptionColorsBrightness(activity, image).execute();
			return true;
		case R.id.action_color_curves_rgb:
			new OptionColorCurves(activity, image, ColorChannelType.RGB).execute();
			return true;
		case R.id.action_color_curves_hsv:
			new OptionColorCurves(activity, image, ColorChannelType.HSV).execute();
			return true;
			
		case R.id.action_settings:
			activity.showSettingsActivity();
			return true;
		}
		return false;
	}
}