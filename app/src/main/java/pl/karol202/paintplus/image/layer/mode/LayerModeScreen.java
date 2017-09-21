package pl.karol202.paintplus.image.layer.mode;

import android.graphics.PorterDuff;

public class LayerModeScreen extends LayerModeSimple
{
	@Override
	protected PorterDuff.Mode getMode()
	{
		return PorterDuff.Mode.SCREEN;
	}
}