package pl.karol202.paintplus.history.action;

import android.graphics.Bitmap;
import android.graphics.Color;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

public class ActionLayerResize extends Action
{
	private int layerId;
	private Bitmap bitmap;
	private int x;
	private int y;
	
	public ActionLayerResize(Image image)
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
		resize(image);
		return true;
	}
	
	@Override
	public boolean redo(Image image)
	{
		if(!super.redo(image)) return false;
		updateBitmap(image);
		resize(image);
		return true;
	}
	
	private void resize(Image image)
	{
		Layer layer = image.getLayerAtIndex(layerId);
		
		Bitmap oldBitmap = layer.getBitmap();
		int oldX = layer.getX();
		int oldY = layer.getY();
		
		layer.setBitmap(bitmap);
		layer.setPosition(x, y);
		
		bitmap = oldBitmap;
		x = oldX;
		y = oldY;
	}
	
	@Override
	boolean canApplyAction()
	{
		Layer layer = getImage().getLayerAtIndex(layerId);
		return layerId != -1 && bitmap != null && (bitmap.getWidth() != layer.getWidth() ||
				bitmap.getHeight() != layer.getHeight() || x != layer.getX() || y != layer.getY());
	}
	
	@Override
	public int getActionName()
	{
		return R.string.history_action_layer_resize;
	}
	
	public void setLayerBeforeResize(Layer layer)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history!");
		this.layerId = getImage().getLayerIndex(layer);
		this.bitmap = layer.getBitmap();
		this.x = layer.getX();
		this.y = layer.getY();
		updateBitmap(getImage());
	}
}