package pl.karol202.paintplus.history.action;

import android.graphics.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.selection.Selection;

public class ActionSelectionChange extends Action
{
	private static final float SELECTION_LINE_WIDTH = 2f;
	
	private Region region;
	
	private Paint selectionPaint;
	
	public ActionSelectionChange(Image image)
	{
		super(image);
		createSelectionPaint();
	}
	
	private void createSelectionPaint()
	{
		selectionPaint = new Paint();
		selectionPaint.setColor(Color.DKGRAY);
		selectionPaint.setStyle(Paint.Style.STROKE);
		selectionPaint.setStrokeWidth(SELECTION_LINE_WIDTH);
	}
	
	private void showRegionOnBitmap(Image image)
	{
		getPreviewBitmap().eraseColor(Color.TRANSPARENT);
		getPreviewCanvas().drawBitmap(image.getFullImage(), null, transformImageRect(image), null);
		getPreviewCanvas().drawPath(transformSelectionPath(image, region), selectionPaint);
	}
	
	private RectF transformImageRect(Image image)
	{
		float max = Math.max(image.getWidth(), image.getHeight());
		float ratio = getPreviewRect().width() / max;
		RectF rect = new RectF(0, 0, image.getWidth() * ratio, image.getHeight() * ratio);
		rect.offset(getPreviewRect().centerX() - rect.centerX(), getPreviewRect().centerY() - rect.centerY());
		return rect;
	}
	
	private Path transformSelectionPath(Image image, Region region)
	{
		RectF rect = transformImageRect(image);
		Matrix matrix = new Matrix();
		matrix.postScale(rect.width() / image.getWidth(), rect.height() / image.getHeight());
		matrix.postTranslate(rect.left, rect.top);
		
		Path path = region.getBoundaryPath();
		path.transform(matrix);
		return path;
	}
	
	@Override
	public boolean undo(Image image)
	{
		if(!super.undo(image) || region == null) return false;
		Selection selection = image.getSelection();
		Region newRegion = selection.getRegion();
		selection.setRegion(region);
		region = newRegion;
		
		showRegionOnBitmap(image);
		return true;
	}
	
	@Override
	public boolean redo(Image image)
	{
		if(!super.redo(image) || region == null) return false;
		Selection selection = image.getSelection();
		Region oldRegion = selection.getRegion();
		selection.setRegion(region);
		region = oldRegion;
		
		showRegionOnBitmap(image);
		return true;
	}
	
	@Override
	boolean canApplyAction()
	{
		return region != null;
	}
	
	@Override
	public int getActionName()
	{
		return R.string.history_action_selection_change;
	}
	
	public void setOldRegion()
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history.");
		this.region = new Region(getTemporaryImage().getSelection().getRegion());
		showRegionOnBitmap(getTemporaryImage());
	}
}