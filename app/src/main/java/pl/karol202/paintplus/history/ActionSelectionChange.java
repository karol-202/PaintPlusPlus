package pl.karol202.paintplus.history;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Region;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.selection.Selection;

public class ActionSelectionChange extends Action
{
	private Selection selection;
	private Region oldRegion;
	private Region newRegion;
	private Bitmap bitmap;
	
	public ActionSelectionChange(Selection selection)
	{
		super();
		this.selection = selection;
		this.oldRegion = new Region(selection.getRegion());
		
		createBitmap();
	}
	
	private void createBitmap()
	{
		int bitmapSize = (int) Math.floor(HistoryActionViewHolder.PREVIEW_SIZE_DP * selection.getImage().SCREEN_DENSITY);
		bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
		showOldRegionOnBitmap();
	}
	
	private void showOldRegionOnBitmap()
	{
		bitmap.eraseColor(Color.TRANSPARENT);
	}
	
	private void showNewRegionOnBitmap()
	{
		if(newRegion == null) return;
		bitmap.eraseColor(Color.TRANSPARENT);
	}
	
	@Override
	boolean undo()
	{
		if(!super.undo()) return false;
		selection.setRegion(oldRegion);
		showNewRegionOnBitmap();
		return true;
	}
	
	@Override
	boolean redo()
	{
		if(!super.redo() || newRegion == null) return false;
		selection.setRegion(newRegion);
		showOldRegionOnBitmap();
		return true;
	}
	
	@Override
	Bitmap getActionPreview()
	{
		return bitmap;
	}
	
	@Override
	int getActionName()
	{
		return R.string.history_action_selection_change;
	}
	
	public void setCurrentRegion()
	{
		this.newRegion = new Region(selection.getRegion());
	}
}