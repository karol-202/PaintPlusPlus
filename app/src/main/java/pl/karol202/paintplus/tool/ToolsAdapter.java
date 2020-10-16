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

package pl.karol202.paintplus.tool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import pl.karol202.paintplus.R;

import java.util.ArrayList;
import java.util.List;

public class ToolsAdapter extends RecyclerView.Adapter<ToolsAdapter.ViewHolder>
{
	public interface OnToolSelectListener
	{
		void onToolSelect(Tool tool);
	}

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		private Tool tool;

		private ImageView imageView;
		private TextView textView;

		ViewHolder(View view)
		{
			super(view);
			view.setOnClickListener(this);
			imageView = view.findViewById(R.id.image_item_tool);
			textView = view.findViewById(R.id.text_item_tool);
		}

		void bind(Tool tool)
		{
			this.tool = tool;
			imageView.setImageResource(tool.getIcon());
			textView.setText(tool.getName());
		}

		@Override
		public void onClick(View view)
		{
			for(OnToolSelectListener listener : listeners) listener.onToolSelect(tool);
		}
	}

	private Context context;
	private Tools tools;
	private List<OnToolSelectListener> listeners;

	public ToolsAdapter(Context context, Tools tools)
	{
		this.context = context;
		this.tools = tools;
		this.listeners = new ArrayList<>();
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.item_tool, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position)
	{
		holder.bind(tools.getTool(position));
	}

	@Override
	public int getItemCount()
	{
		return tools.getToolsAmount();
	}

	public void addOnToolSelectListener(OnToolSelectListener listener)
	{
		listeners.add(listener);
	}
}
