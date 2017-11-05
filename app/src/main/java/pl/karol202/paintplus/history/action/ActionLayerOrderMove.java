package pl.karol202.paintplus.history.action;

import android.graphics.Color;
import android.graphics.RectF;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

public class ActionLayerOrderMove extends Action
{
	private int sourceLayerPos;
	private int destinationLayerPos;
	
	public ActionLayerOrderMove(Image image)
	{
		super(image);
		this.sourceLayerPos = -1;
		this.destinationLayerPos = -1;
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
		Layer layer = image.getLayerAtIndex(destinationLayerPos);
		image.deleteLayer(layer);
		
		image.addLayer(layer, sourceLayerPos);
		return true;
	}
	
	@Override
	public boolean redo(Image image)
	{
		if(!super.redo(image)) return false;
		Layer layer = image.getLayerAtIndex(sourceLayerPos);
		image.deleteLayer(layer);
		
		image.addLayer(layer, destinationLayerPos);
		return true;
	}
	
	@Override
	boolean canApplyAction()
	{
		return sourceLayerPos != -1 && destinationLayerPos != -1 && sourceLayerPos != destinationLayerPos;
	}
	
	@Override
	public int getActionName()
	{
		return R.string.history_action_layer_order_move;
	}
	
	public void setSourceAndDestinationLayerPos(int sourcePos, int destinationPos)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history.");
		this.sourceLayerPos = sourcePos;
		this.destinationLayerPos = destinationPos;
		updateBitmap(getImage());
	}
}