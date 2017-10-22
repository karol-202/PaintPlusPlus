package pl.karol202.paintplus.history;

import android.graphics.Bitmap;
import pl.karol202.paintplus.image.Image;

public abstract class Action
{
	protected boolean applied;
	private boolean done;
	
	Action()
	{
		applied = false;
		done = true;
	}
	
	boolean undo(Image image)
	{
		if(!done || !applied) return false;
		done = false;
		return true;
	}
	
	boolean redo(Image image)
	{
		if(done || !applied) return false;
		done = true;
		return true;
	}
	
	public void applyAction(Image image)
	{
		image.addHistoryAction(this);
		applied = true;
	}
	
	abstract Bitmap getActionPreview();
	
	abstract int getActionName();
}