package pl.karol202.paintplus.history;

import android.graphics.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.selection.Selection;

public class ActionSelectionChange extends Action
{
	private static final float SELECTION_LINE_WIDTH = 2f;
	
	private Region region;
	
	private Bitmap bitmap;
	private Canvas canvas;
	private Rect bitmapRect;
	private Paint selectionPaint;
	
	public ActionSelectionChange(Image image)
	{
		super();
		createBitmap(image);
		createSelectionPaint();
	}
	
	private void createBitmap(Image image)
	{
		int bitmapSize = (int) Math.floor(HistoryActionViewHolder.PREVIEW_SIZE_DP * image.SCREEN_DENSITY);
		bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		bitmapRect = new Rect(0, 0, bitmapSize, bitmapSize);
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
		bitmap.eraseColor(Color.TRANSPARENT);
		canvas.drawBitmap(image.getFullImage(), null, transformImageRect(image), null);
		canvas.drawPath(transformSelectionPath(image, region), selectionPaint);
	}
	
	private RectF transformImageRect(Image image)
	{
		float max = Math.max(image.getWidth(), image.getHeight());
		float ratio = bitmapRect.width() / max;
		RectF rect = new RectF(0, 0, image.getWidth() * ratio, image.getHeight() * ratio);
		rect.offset(bitmapRect.centerX() - rect.centerX(), bitmapRect.centerY() - rect.centerY());
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
	boolean undo(Image image)
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
	boolean redo(Image image)
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
	public void applyAction(Image image)
	{
		if(region != null) super.applyAction(image);
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
	
	public void setOldRegion(Image image)
	{
		if(applied) throw new IllegalStateException("Cannot alter history.");
		this.region = new Region(image.getSelection().getRegion());
		showRegionOnBitmap(image);
	}
}