package pl.karol202.paintplus.history.action;

import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

public class ActionLayerRotate extends ActionLayerResize
{
	public ActionLayerRotate(Image image)
	{
		super(image);
	}
	
	@Override
	public int getActionName()
	{
		return R.string.history_action_layer_rotate;
	}
	
	public void setLayerBeforeRotation(Layer layer)
	{
		setLayerBeforeResize(layer);
	}
}