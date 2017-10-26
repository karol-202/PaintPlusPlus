package pl.karol202.paintplus.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.Action;

class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder>
{
	private static final int VIEW_TYPE_ACTION = 0;
	private static final int VIEW_TYPE_CURRENT_POS = 1;
	
	private Context context;
	private History history;
	
	HistoryAdapter(Context context, History history)
	{
		this.context = context;
		this.history = history;
	}
	
	@Override
	public HistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		int layout = viewType == VIEW_TYPE_ACTION ? R.layout.item_history_action : R.layout.item_history_current_position;
		View view = LayoutInflater.from(context).inflate(layout, viewGroup, false);
		
		if(viewType == VIEW_TYPE_ACTION) return new HistoryActionViewHolder(view);
		else return new HistoryCurrentPositionViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(HistoryViewHolder viewHolder, int position)
	{
		viewHolder.bind(history, getActionAtPosition(position));
	}
	
	private Action getActionAtPosition(int pos)
	{
		if(pos < history.getFollowingActionsAmount()) return history.getFollowingAction(pos);
		else if(pos > history.getFollowingActionsAmount()) return history.getPreviousAction(getItemCount() - 1 - pos);
		else return null;
	}
	
	@Override
	public int getItemCount()
	{
		return history.getPreviousActionsAmount() + history.getFollowingActionsAmount() + 1;
	}
	
	@Override
	public int getItemViewType(int position)
	{
		return position == history.getFollowingActionsAmount() ? VIEW_TYPE_CURRENT_POS : VIEW_TYPE_ACTION;
	}
}