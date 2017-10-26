package pl.karol202.paintplus.history;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import pl.karol202.paintplus.history.action.Action;

abstract class HistoryViewHolder extends RecyclerView.ViewHolder
{
	HistoryViewHolder(View itemView)
	{
		super(itemView);
	}
	
	void bind(History history, Action action) { }
}