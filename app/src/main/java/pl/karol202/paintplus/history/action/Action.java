package pl.karol202.paintplus.history.action;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import pl.karol202.paintplus.history.HistoryActionViewHolder;
import pl.karol202.paintplus.image.Image;

public abstract class Action
{
	private boolean done;
	
	private boolean applied;
	private Image temporaryImage;
	
	private Bitmap previewBitmap;
	private Canvas previewCanvas;
	private Rect previewRect;
	
	Action(Image image)
	{
		done = true;
		
		applied = false;
		temporaryImage = image;
		createBitmap(image);
	}
	
	private void createBitmap(Image image)
	{
		int bitmapSize = (int) Math.floor(HistoryActionViewHolder.PREVIEW_SIZE_DP * image.SCREEN_DENSITY);
		previewBitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
		previewCanvas = new Canvas(previewBitmap);
		previewRect = new Rect(0, 0, bitmapSize, bitmapSize);
	}
	
	public boolean undo(Image image)
	{
		if(!done || !applied) return false;
		done = false;
		return true;
	}
	
	public boolean redo(Image image)
	{
		if(done || !applied) return false;
		done = true;
		return true;
	}
	
	public void applyAction()
	{
		if(!canApplyAction()) return;
		temporaryImage.addHistoryAction(this);
		applied = true;
		temporaryImage = null;
	}
	
	abstract boolean canApplyAction();
	
	public Bitmap getActionPreview()
	{
		return previewBitmap;
	}
	
	boolean isApplied()
	{
		return applied;
	}
	
	Image getTemporaryImage()
	{
		return temporaryImage;
	}
	
	Bitmap getPreviewBitmap()
	{
		return previewBitmap;
	}
	
	Canvas getPreviewCanvas()
	{
		return previewCanvas;
	}
	
	Rect getPreviewRect()
	{
		return previewRect;
	}
	
	public abstract int getActionName();
}