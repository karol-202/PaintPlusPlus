package pl.karol202.paintplus.history;

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
	
	boolean canActionBeUndoneNow(Action action)
	{
		return !previousActions.empty() && previousActions.peek() == action;
	}
	
	boolean canActionBeRedoneNow(Action action)
	{
		return !followingActions.empty() && followingActions.peek() == action;
	}
	
	public void undo()
	{
		Action action = previousActions.pop();
		if(!action.undo()) throw new IllegalStateException("Cannot undo this action.");
		followingActions.push(action);
		if(listener != null) listener.onHistoryUpdated();
	}
	
	public void redo()
	{
		Action action = followingActions.pop();
		if(!action.redo()) throw new IllegalStateException("Cannot redo this action.");
		previousActions.push(action);
		if(listener != null) listener.onHistoryUpdated();
	}
	
	public void addAction(Action action)
	{
		previousActions.push(action);
		followingActions.clear();
		if(listener != null) listener.onHistoryUpdated();
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