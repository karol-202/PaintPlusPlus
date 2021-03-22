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
package pl.karol202.paintplus.history

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.karol202.paintplus.databinding.ItemHistoryActionBinding
import pl.karol202.paintplus.databinding.ItemHistoryCurrentPositionBinding
import pl.karol202.paintplus.history.action.Action
import pl.karol202.paintplus.image.HistoryState
import pl.karol202.paintplus.util.layoutInflater

private const val VIEW_TYPE_ACTION = 0
private const val VIEW_TYPE_CURRENT_POS = 1

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>()
{
	abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
	{
		abstract fun bind(action: Action)
	}

	class ActionViewHolder(private val views: ItemHistoryActionBinding) : ViewHolder(views.root)
	{
		override fun bind(action: Action)
		{
			// TODO Image
			views.textHistoryAction.setText(action.name)
		}
	}

	class CursorViewHolder(views: ItemHistoryCurrentPositionBinding) : ViewHolder(views.root)
	{
		override fun bind(action: Action) { }
	}

	var historyState = HistoryState()
		set(value)
		{
			field = value
			notifyDataSetChanged()
		}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(viewType)
	{
		VIEW_TYPE_ACTION -> ActionViewHolder(ItemHistoryActionBinding.inflate(parent.context.layoutInflater, parent, false))
		else -> CursorViewHolder(ItemHistoryCurrentPositionBinding.inflate(parent.context.layoutInflater, parent, false))
	}

	override fun onBindViewHolder(viewHolder: ViewHolder, position: Int)
	{
		val action = getActionAtPosition(position) ?: return
		viewHolder.bind(action)
	}

	private fun getActionAtPosition(position: Int) = when
	{
		position < historyState.followingSize -> historyState.followingActions[position]
		position > historyState.precedingSize -> historyState.precedingActions[itemCount - 1 - position]
		else -> null
	}

	override fun getItemCount() =
			historyState.precedingSize + historyState.followingSize + 1

	override fun getItemViewType(position: Int) =
			if(position == historyState.followingSize) VIEW_TYPE_CURRENT_POS else VIEW_TYPE_ACTION
}
