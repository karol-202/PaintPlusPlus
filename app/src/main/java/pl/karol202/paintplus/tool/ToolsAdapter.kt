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
package pl.karol202.paintplus.tool

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import pl.karol202.paintplus.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import pl.karol202.paintplus.databinding.ItemToolBinding
import pl.karol202.paintplus.util.layoutInflater
import java.util.ArrayList

class ToolsAdapter(private val context: Context,
                   private val tools: Tools,
                   private val listener: (Tool) -> Unit) : RecyclerView.Adapter<ToolsAdapter.ViewHolder>()
{
	inner class ViewHolder(private val views: ItemToolBinding) : RecyclerView.ViewHolder(views.root)
	{
		fun bind(tool: Tool)
		{
			views.root.setOnClickListener { listener(tool) }
			views.imageItemTool.setImageResource(tool.icon)
			views.textItemTool.setText(tool.name)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
			ViewHolder(ItemToolBinding.inflate(context.layoutInflater, parent, false))

	override fun onBindViewHolder(holder: ViewHolder, position: Int) =
			holder.bind(tools.getTool(position))

	override fun getItemCount() = tools.tools.size
}