package pl.karol202.paintplus.history.action;

import android.graphics.Bitmap;
import android.graphics.Color;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.image.layer.mode.LayerMode;

public class ActionLayerPropertiesChange extends Action
{
	private int layerId;
	private LayerMode mode;
	private float opacity;
	
	public ActionLayerPropertiesChange(Image image)
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
		
		LayerMode newMode = layer.getMode();
		float newOpacity = layer.getOpacity();
		
		layer.setMode(mode);
		layer.setOpacity(opacity);
		
		mode = newMode;
		opacity = newOpacity;
		
		return true;
	}
	
	@Override
	public boolean redo(Image image)
	{
		if(!super.redo(image)) return false;
		Layer layer = image.getLayerAtIndex(layerId);
		
		LayerMode oldMode = layer.getMode();
		float oldOpacity = layer.getOpacity();
		
		layer.setMode(mode);
		layer.setOpacity(opacity);
		
		mode = oldMode;
		opacity = oldOpacity;
		
		return true;
	}
	
	@Override
	boolean canApplyAction()
	{
		Layer layer = getImage().getLayerAtIndex(layerId);
		return layerId != -1 && (mode != layer.getMode() || opacity != layer.getOpacity());
	}
	
	@Override
	public int getActionName()
	{
		return R.string.history_action_layer_properties_change;
	}
	
	public void setLayerBeforeChange(Layer layer)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history.");
		this.layerId = getImage().getLayerIndex(layer);
		this.mode = layer.getMode();
		this.opacity = layer.getOpacity();
		updateBitmap(getImage());
	}
}