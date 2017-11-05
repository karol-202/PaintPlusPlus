package pl.karol202.paintplus.history.action;

import android.graphics.Color;
import android.graphics.RectF;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;

public class ActionImageFlip extends Action
{
	private int direction;
	
	public ActionImageFlip(Image image)
	{
		super(image);
		this.direction = -1;
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
		updateBitmap(image);
		image.flip(direction);
		return true;
	}
	
	@Override
	public boolean redo(Image image)
	{
		if(!super.redo(image)) return false;
		updateBitmap(image);
		image.flip(direction);
		return true;
	}
	
	@Override
	boolean canApplyAction()
	{
		return direction != -1;
	}
	
	@Override
	public int getActionName()
	{
		return R.string.history_action_image_flip;
	}
	
	public void setDirectionBeforeFlip(int direction)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history!");
		this.direction = direction;
		updateBitmap(getImage());
	}
}