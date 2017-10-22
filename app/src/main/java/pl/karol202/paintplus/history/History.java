package pl.karol202.paintplus.history;

import pl.karol202.paintplus.image.Image;

import java.util.Stack;

public class History
{
	private OnHistoryUpdateListener listener;
	private Stack<Action> previousActions;
	private Stack<Action> followingActions;
	
	public History()
	{
		previousActions = new Stack<>();
		followingActions = new Stack<>();
	}
	
	public boolean canUndo()
	{
		return !previousActions.empty();
	}
	
	public boolean canRedo()
	{
		return !followingActions.empty();
	}
	
	public void undo(Image image)
	{
		Action action = previousActions.pop();
		if(!action.undo(image)) throw new IllegalStateException("Cannot undo this action.");
		followingActions.push(action);
		if(listener != null) listener.onHistoryUpdated();
	}
	
	public void redo(Image image)
	{
		Action action = followingActions.pop();
		if(!action.redo(image)) throw new IllegalStateException("Cannot redo this action.");
		previousActions.push(action);
		if(listener != null) listener.onHistoryUpdated();
	}
	
	public void addAction(Action action)
	{
		previousActions.push(action);
		followingActions.clear();
		if(listener != null) listener.onHistoryUpdated();
	}
	
	public void clear()
	{
		previousActions.clear();
		followingActions.clear();
	}
	
	int getPreviousActionsAmount()
	{
		return previousActions.size();
	}
	
	int getFollowingActionsAmount()
	{
		return followingActions.size();
	}
	
	Action getPreviousAction(int i)
	{
		return previousActions.get(i);
	}
	
	Action getFollowingAction(int i)
	{
		return followingActions.get(i);
	}
	
	public void setHistoryUpdateListener(OnHistoryUpdateListener listener)
	{
		this.listener = listener;
	}
}