package pl.karol202.paintplus.options;

import android.content.Context;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionImageFlip;
import pl.karol202.paintplus.image.Image;

public class OptionImageFlip extends OptionFlip
{
	public OptionImageFlip(Context context, Image image)
	{
		super(context, image);
	}
	
	@Override
	protected int getTitle()
	{
		return R.string.dialog_flip_image;
	}
	
	@Override
	protected void flip(int direction)
	{
		ActionImageFlip action = new ActionImageFlip(image);
		action.setDirectionBeforeFlip(direction);
		
		image.flip(direction);
		
		action.applyAction();
	}
}