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
package pl.karol202.paintplus.recent

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.graphics.Bitmap
import pl.karol202.paintplus.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import pl.karol202.paintplus.databinding.CardRecentBinding
import pl.karol202.paintplus.util.layoutInflater

class RecentAdapter(private val context: Context,
                    private val onSelect: (RecentImage) -> Unit) : RecyclerView.Adapter<RecentAdapter.ViewHolder>()
{
	inner class ViewHolder(private val views: CardRecentBinding) : RecyclerView.ViewHolder(views.root)
	{
		var image: RecentImage? = null
			private set

		fun bind(image: RecentImage)
		{
			this.image = image

			views.textRecentName.text = image.name
			views.imageRecentThumbnail.setImageURI(image.uri)
			views.buttonRecentOpen.setOnClickListener { onSelect(image) }
		}
	}

	private class DiffCallback(private val oldList: List<RecentImage>,
	                           private val newList: List<RecentImage>) : DiffUtil.Callback() {
		override fun getOldListSize() = oldList.size

		override fun getNewListSize() = newList.size

		override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
				oldList[oldItemPosition].uri == newList[newItemPosition].uri

		override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
				oldList[oldItemPosition] == newList[newItemPosition]
	}

	var images = emptyList<RecentImage>()
		set(value)
		{
			val result = DiffUtil.calculateDiff(DiffCallback(field, value))
			field = value
			result.dispatchUpdatesTo(this)
		}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
			ViewHolder(CardRecentBinding.inflate(context.layoutInflater, parent, false))

	override fun onBindViewHolder(holder: ViewHolder, position: Int) =
			holder.bind(images[position])

	override fun getItemCount() = images.size
}
