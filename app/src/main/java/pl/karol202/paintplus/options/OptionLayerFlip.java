package pl.karol202.paintplus.options;

import android.content.Context;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionLayerFlip;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

public class OptionLayerFlip extends OptionFlip
{
	private Layer layer;
	
	public OptionLayerFlip(Context context, Image image)
	{
		super(context, image);
		this.layer = image.getSelectedLayer();
	}
	
	@Override
	protected int getTitle()
	{
		return R.string.dialog_flip_layer;
	}
	
	@Override
	protected void flip(int direction)
	{
		ActionLayerFlip action = new ActionLayerFlip(image);
		action.setLayerAndFlipDirection(image.getLayerIndex(layer), direction);
		
		layer.flip(direction);
		
		action.applyAction();
	}
}