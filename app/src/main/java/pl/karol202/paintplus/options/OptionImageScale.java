package pl.karol202.paintplus.options;

import android.content.Context;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionImageScale;
import pl.karol202.paintplus.image.Image;

public class OptionImageScale extends OptionScale
{
	public OptionImageScale(Context context, Image image)
	{
		super(context, image);
	}
	
	@Override
	protected int getTitle()
	{
		return R.string.dialog_scale_image;
	}
	
	@Override
	protected int getObjectWidth()
	{
		return image.getWidth();
	}
	
	@Override
	protected int getObjectHeight()
	{
		return image.getHeight();
	}
	
	@Override
	protected void applySize(int width, int height, boolean smooth)
	{
		ActionImageScale action = new ActionImageScale(image);
		
		image.scale(width, height, smooth);
		
		action.applyAction();
	}
}