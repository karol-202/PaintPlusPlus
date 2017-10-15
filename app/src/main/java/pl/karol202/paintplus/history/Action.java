package pl.karol202.paintplus.history;

import android.graphics.Bitmap;
import pl.karol202.paintplus.image.Image;

public abstract class Action
{
	private boolean done;
	
	Action()
	{
		done = true;
	}
	
	boolean undo(Image image)
	{
		if(!done) return false;
		done = false;
		return true;
	}
	
	boolean redo(Image image)
	{
		if(done) return false;
		done = true;
		return true;
	}
	
	abstract Bitmap getActionPreview();
	
	abstract int getActionName();
	
	boolean isActionDone()
	{
		return done;
	}
}