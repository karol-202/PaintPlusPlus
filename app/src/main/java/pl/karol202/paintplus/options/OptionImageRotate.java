package pl.karol202.paintplus.options;

import android.content.Context;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;

@Deprecated
public class OptionImageRotate extends OptionRotate
{
	public OptionImageRotate(Context context, Image image)
	{
		super(context, image);
	}
	
	@Override
	protected int getTitle()
	{
		return R.string.dialog_rotate_image;
	}
	
	@Override
	protected void rotate(float angle)
	{
		//Will be added soon.
	}
}