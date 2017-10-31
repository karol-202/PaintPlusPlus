package pl.karol202.paintplus.history.action;

import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;

public class ActionPaste extends ActionLayerAdd
{
	public ActionPaste(Image image)
	{
		super(image);
	}
	
	@Override
	public int getActionName()
	{
		return R.string.history_action_paste;
	}
}