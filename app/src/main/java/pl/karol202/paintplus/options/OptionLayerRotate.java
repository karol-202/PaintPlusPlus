package pl.karol202.paintplus.options;

import android.content.Context;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Layer;

public class OptionLayerRotate extends OptionRotate
{
	private Layer layer;
	
	public OptionLayerRotate(Context context, Image image)
	{
		super(context, image);
		this.layer = image.getSelectedLayer();
	}
	
	@Override
	protected int getTitle()
	{
		return R.string.dialog_rotate_layer;
	}
	
	@Override
	protected void rotate(float angle)
	{
		layer.rotate(angle);
	}
}