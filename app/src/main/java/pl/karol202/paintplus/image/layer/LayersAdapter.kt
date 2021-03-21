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
package pl.karol202.paintplus.image.layer

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.karol202.paintplus.databinding.ItemLayerBinding
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.util.layoutInflater

class LayersAdapter(private val context: Context,
                    mainContainer: ViewGroup,
                    onScrollBlock: (Boolean) -> Unit,
                    private val onInfoShow: (Layer) -> Unit,
                    private val onPropertiesEdit: (Layer) -> Unit,
                    private val onNameChange: (Layer) -> Unit,
                    private val onSelect: (Layer) -> Unit,
                    private val onVisibilityToggle: (Layer) -> Unit,
                    onMove: (layerIndex: Int, target: Int) -> Unit,
                    private val onDuplicate: (Layer) -> Unit,
                    private val onMerge: (Layer) -> Unit,
                    private val onDelete: (Layer) -> Unit) : RecyclerView.Adapter<LayerViewHolder>()
{
	private val viewHolders = mutableMapOf<Int, LayerViewHolder>()
	private val layerHandle = LayerHandle(context, mainContainer, viewHolders, onMove, onScrollBlock)

	var image: Image? = null
		set(value)
		{
			field = value
			notifyDataSetChanged()
		}
	private val layers get() = image?.layers ?: emptyList()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
			LayerViewHolder(views = ItemLayerBinding.inflate(context.layoutInflater, parent, false),
			                layerHandle = layerHandle,
			                onInfoShow = onInfoShow,
			                onPropertiesEdit = onPropertiesEdit,
			                onNameChange = onNameChange,
			                onSelect = onSelect,
			                onVisibilityToggle = onVisibilityToggle,
			                onDuplicate = onDuplicate,
			                onMerge = onMerge,
			                onDelete = onDelete)

	override fun onBindViewHolder(holder: LayerViewHolder, position: Int)
	{
		val image = image ?: return
		val layer = layers[position]
		holder.bind(layer = layer,
		            isLast = position == layers.size - 1,
		            isSelected = image.isLayerSelected(layer),
		            areLayersLocked = image.layersLocked)
		viewHolders[position] = holder
	}

	override fun getItemCount() = layers.size
}
