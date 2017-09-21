package pl.karol202.paintplus.image.layer.mode;

import android.graphics.PorterDuff;
import pl.karol202.paintplus.image.layer.Layer;

public class LayerModeDefault extends LayerModeSimple
{
	public LayerModeDefault()
	{
		super();
	}
	
	public LayerModeDefault(Layer layer)
	{
		super(layer);
	}
	
	@Override
	protected PorterDuff.Mode getMode()
	{
		return PorterDuff.Mode.SRC_OVER;
	}
}