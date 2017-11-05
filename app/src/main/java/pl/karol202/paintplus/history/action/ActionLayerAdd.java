package pl.karol202.paintplus.history.action;

import android.graphics.Bitmap;
import android.graphics.Color;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

public class ActionLayerAdd extends Action
{
	private Layer layer;
	private int layerPosition;
	
	public ActionLayerAdd(Image image)
	{
		super(image);
	}
	
	private void updateBitmap()
	{
		Bitmap layerBitmap = layer.getBitmap();
		getPreviewBitmap().eraseColor(Color.TRANSPARENT);
		getPreviewCanvas().drawBitmap(layerBitmap, null, transformLayerRect(layerBitmap), null);
	}
	
	@Override
	public boolean undo(Image image)
	{
		if(!super.undo(image)) return false;
		image.deleteLayer(layer);
		return true;
	}
	
	@Override
	public boolean redo(Image image)
	{
		if(!super.redo(image)) return false;
		image.addLayer(layer, layerPosition);
		return true;
	}
	
	@Override
	boolean canApplyAction()
	{
		return layer != null;
	}
	
	@Override
	public int getActionName()
	{
		return R.string.history_action_layer_add;
	}
	
	public void setLayerAfterAdding(Layer layer)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history.");
		this.layer = layer;
		this.layerPosition = getImage().getLayerIndex(layer);
		updateBitmap();
	}
}