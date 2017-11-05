package pl.karol202.paintplus.options;

import android.content.Context;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionImageResize;
import pl.karol202.paintplus.image.Image;

public class OptionImageResize extends OptionResize
{
	public OptionImageResize(Context context, Image image)
	{
		super(context, image);
	}
	
	@Override
	protected int getTitle()
	{
		return R.string.dialog_resize_image;
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
	protected int getOldObjectWidth()
	{
		return image.getWidth();
	}
	
	@Override
	protected int getOldObjectHeight()
	{
		return image.getHeight();
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
		ActionImageResize action = new ActionImageResize(image);
		action.setDataBeforeResizing(image.getWidth(), image.getHeight(), x, y);
		
		image.resize(x, y, width, height);
		
		action.applyAction();
	}
}