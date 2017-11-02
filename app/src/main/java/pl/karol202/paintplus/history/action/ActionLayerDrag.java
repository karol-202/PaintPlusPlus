package pl.karol202.paintplus.history.action;

import android.graphics.Bitmap;
import android.graphics.Color;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

public class ActionLayerDrag extends Action
{
	private int layerId;
	private int deltaX;
	private int deltaY;
	
	public ActionLayerDrag(Image image)
	{
		super(image);
		this.layerId = -1;
	}
	
	private void updateBitmap(Image image)
	{
		Bitmap layerBitmap = image.getLayerAtIndex(layerId).getBitmap();
		getPreviewBitmap().eraseColor(Color.TRANSPARENT);
		getPreviewCanvas().drawBitmap(layerBitmap, null, transformLayerRect(layerBitmap), null);
	}
	
	@Override
	public boolean undo(Image image)
	{
		if(!super.undo(image)) return false;
		Layer layer = image.getLayerAtIndex(layerId);
		layer.setPosition(layer.getX() - deltaX, layer.getY() - deltaY);
		return true;
	}
	
	@Override
	public boolean redo(Image image)
	{
		if(!super.redo(image)) return false;
		Layer layer = image.getLayerAtIndex(layerId);
		layer.setPosition(layer.getX() + deltaX, layer.getY() + deltaY);
		return true;
	}
	
	@Override
	boolean canApplyAction()
	{
		return layerId != -1 && deltaX != 0 && deltaY != 0;
	}
	
	@Override
	public int getActionName()
	{
		return R.string.history_action_layer_drag;
	}
	
	public void setLayerAndDragDelta(Layer layer, int deltaX, int deltaY)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history!");
		this.layerId = getTemporaryImage().getLayerIndex(layer);
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		updateBitmap(getTemporaryImage());
	}
}