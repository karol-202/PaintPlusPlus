package pl.karol202.paintplus.history.action;

import android.graphics.Bitmap;
import android.graphics.Color;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

public class ActionLayerFlip extends Action
{
	private int layerId;
	private int direction;
	
	public ActionLayerFlip(Image image)
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
		updateBitmap(image);
		flip(image);
		return true;
	}
	
	@Override
	public boolean redo(Image image)
	{
		if(!super.redo(image)) return false;
		updateBitmap(image);
		flip(image);
		return true;
	}
	
	private void flip(Image image)
	{
		Layer layer = image.getLayerAtIndex(layerId);
		layer.flip(direction);
	}
	
	@Override
	boolean canApplyAction()
	{
		return layerId != -1;
	}
	
	@Override
	public int getActionName()
	{
		return R.string.history_action_layer_flip;
	}

	public void setLayerAndFlipDirection(int layerId, int direction)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history!");
		this.layerId = layerId;
		this.direction = direction;
		updateBitmap(getImage());
	}
}