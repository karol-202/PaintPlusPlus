package pl.karol202.paintplus.options;

import android.content.Context;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

public class OptionLayerScale extends OptionScale
{
	private Layer layer;
	
	public OptionLayerScale(Context context, Image image)
	{
		super(context, image);
		this.layer = image.getSelectedLayer();
	}
	
	@Override
	protected int getTitle()
	{
		return R.string.dialog_scale_layer;
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
	protected void applySize(int width, int height, boolean smooth)
	{
		layer.scale(width, height, smooth);
	}
}