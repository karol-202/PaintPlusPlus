package pl.karol202.paintplus.history.action;

import android.graphics.Bitmap;
import android.graphics.Color;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

public class ActionLayerVisibilityChange extends Action
{
	private int layerId;
	private boolean hideLayer;//True, if action caused hide of the layer and undo will show the layer again.
	
	public ActionLayerVisibilityChange(Image image)
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
		layer.setVisibility(hideLayer);
		return true;
	}
	
	@Override
	public boolean redo(Image image)
	{
		if(!super.redo(image)) return false;
		Layer layer = image.getLayerAtIndex(layerId);
		layer.setVisibility(!hideLayer);
		return true;
	}
	
	@Override
	boolean canApplyAction()
	{
		Layer layer = getImage().getLayerAtIndex(layerId);
		return layerId != -1 && hideLayer != layer.isVisible();
	}
	
	@Override
	public int getActionName()
	{
		return hideLayer ? R.string.history_action_layer_hide : R.string.history_action_layer_show;
	}
	
	public void setLayerBeforeChange(Layer layer)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history.");
		this.layerId = getImage().getLayerIndex(layer);
		this.hideLayer = layer.isVisible();
		updateBitmap(getImage());
	}
}