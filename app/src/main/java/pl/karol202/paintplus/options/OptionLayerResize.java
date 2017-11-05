package pl.karol202.paintplus.options;

import android.content.Context;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionLayerResize;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

public class OptionLayerResize extends OptionResize
{
	private Layer layer;
	
	public OptionLayerResize(Context context, Image image)
	{
		super(context, image);
		this.layer = image.getSelectedLayer();
	}
	
	@Override
	protected int getTitle()
	{
		return R.string.dialog_resize_layer;
	}
	
	@Override
	protected int getObjectWidth()
	{
		return layer.getWidth();
	}
	
	@Override
	protected int getObjectHeight()
	{
		return layer.getHeight();
	}
	
	@Override
	protected int getOldObjectWidth()
	{
		return layer.getWidth();
	}
	
	@Override
	protected int getOldObjectHeight()
	{
		return layer.getHeight();
	}
	
	@Override
	protected int getObjectX()
	{
		return 0;
	}
	
	@Override
	protected int getObjectY()
	{
		return 0;
	}
	
	@Override
	protected void applySize(int x, int y, int width, int height)
	{
		ActionLayerResize action = new ActionLayerResize(image);
		action.setLayerBeforeResize(layer);
		
		layer.resize(x, y, width, height);
		
		action.applyAction();
	}
}