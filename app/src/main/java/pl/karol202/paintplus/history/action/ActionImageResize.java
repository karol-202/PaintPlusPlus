package pl.karol202.paintplus.history.action;

import android.graphics.Color;
import android.graphics.RectF;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;

public class ActionImageResize extends Action
{
	private int width;
	private int height;
	private int resizingDeltaX;
	private int resizingDeltaY;
	
	public ActionImageResize(Image image)
	{
		super(image);
		width = -1;
		height = -1;
	}
	
	private void updateBitmap(Image image)
	{
		getPreviewBitmap().eraseColor(Color.TRANSPARENT);
		getPreviewCanvas().drawBitmap(image.getFullImage(), null, transformImageRect(image), null);
	}
	
	private RectF transformImageRect(Image image)
	{
		float max = Math.max(image.getWidth(), image.getHeight());
		float ratio = getPreviewRect().width() / max;
		RectF rect = new RectF(0, 0, image.getWidth() * ratio, image.getHeight() * ratio);
		rect.offset(getPreviewRect().centerX() - rect.centerX(), getPreviewRect().centerY() - rect.centerY());
		return rect;
	}
	
	@Override
	public boolean undo(Image image)
	{
		if(!super.undo(image)) return false;
		int newWidth = image.getWidth();
		int newHeight = image.getHeight();
		image.resize(-resizingDeltaX, -resizingDeltaY, width, height);
		width = newWidth;
		height = newHeight;
		return true;
	}
	
	@Override
	public boolean redo(Image image)
	{
		if(!super.redo(image)) return false;
		int oldWidth = image.getWidth();
		int oldHeight = image.getHeight();
		image.resize(resizingDeltaX, resizingDeltaY, width, height);
		width = oldWidth;
		height = oldHeight;
		return true;
	}
	
	@Override
	boolean canApplyAction()
	{
		return width != -1 && height != -1 && (width != getImage().getWidth() || height != getImage().getHeight() ||
											   resizingDeltaX != 0 || resizingDeltaY != 0);
	}
	
	@Override
	public int getActionName()
	{
		return R.string.history_action_image_resize;
	}

	public void setDataBeforeResizing(int oldWidth, int oldHeight, int resizingDeltaX, int resizingDeltaY)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history!");
		this.width = oldWidth;
		this.height = oldHeight;
		this.resizingDeltaX = resizingDeltaX;
		this.resizingDeltaY = resizingDeltaY;
		updateBitmap(getImage());
	}
}