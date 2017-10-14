package pl.karol202.paintplus.history;

import android.graphics.Bitmap;

public abstract class Action
{
	private boolean done;
	
	Action()
	{
		done = true;
	}
	
	boolean undo()
	{
		if(!done) return false;
		done = false;
		return true;
	}
	
	boolean redo()
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