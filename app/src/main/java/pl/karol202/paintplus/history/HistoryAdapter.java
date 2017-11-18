/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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