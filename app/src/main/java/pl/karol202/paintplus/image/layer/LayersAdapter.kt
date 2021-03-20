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

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.karol202.paintplus.activity.ActivityPaint
import pl.karol202.paintplus.databinding.ItemLayerBinding
import pl.karol202.paintplus.image.Image

class LayersAdapter(private val activity: ActivityPaint,
                    private val onInfoShow: (Layer) -> Unit,
                    private val onPropertiesEdit: (Layer) -> Unit,
                    private val onNameChange: (Layer) -> Unit,
                    private val onSelect: (Layer) -> Unit,
                    private val onVisibilityToggle: (Layer) -> Unit,
                    private val onMove: (layerIndex: Int, target: Int) -> Unit,
                    private val onDuplicate: (Layer) -> Unit,
                    private val onMerge: (Layer) -> Unit,
                    private val onDelete: (Layer) -> Unit) : RecyclerView.Adapter<LayerViewHolder>()
{
	//private val DUPLICATE_INDICATOR = context.getString(R.string.duplicate)

	private val viewHolders = mutableMapOf<Int, LayerViewHolder>()
	private val layerHandle = LayerHandle(activity, viewHolders, onMove)

	var image: Image? = null
		set(value)
		{
			field = value
			notifyDataSetChanged()
		}
	private val layers get() = image?.layers ?: emptyList()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
			LayerViewHolder(views = ItemLayerBinding.inflate(activity.layoutInflater, parent, false),
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

	fun moveLayer(layerIndex: Int, target: Int) = onMove(layerIndex, target)
	/*{
		val selected = image!!.selectedLayer
		val layer = layers!!.removeAt(layerIndex)
		layers!!.add(target, layer)
		image!!.selectLayer(layers!!.indexOf(selected))
		image!!.updateImage()
		val action = ActionLayerOrderMove(image)
		action.setSourceAndDestinationLayerPos(layerIndex, target)
		action.applyAction()
	}*/

	/*fun duplicateLayer(layer: Layer)
	{
		val layerIndex = layers!!.indexOf(layer)
		val newName = layer.name + DUPLICATE_INDICATOR
		val newLayer = Layer(layer.x, layer.y, newName, layer.width, layer.height, Color.BLACK)
		val newBitmap = Bitmap.createBitmap(layer.bitmap)
		newLayer.setBitmap(newBitmap)
		newLayer.setMode(copyLayerMode(layer.mode))
		newLayer.setOpacity(layer.opacity)
		if(!image!!.addLayer(newLayer, layerIndex)) appContext.createSnackbar(R.string.too_many_layers, Toast.LENGTH_SHORT)
				.show()
		else createDuplicateHistoryAction(newLayer)
	}

	private fun createDuplicateHistoryAction(newLayer: Layer)
	{
		val action = ActionLayerDuplicate(image)
		action.setLayerAfterAdding(newLayer)
		action.applyAction()
	}

	fun joinWithNextLayer(firstLayer: Layer)
	{
		val firstIndex = layers!!.indexOf(firstLayer)
		val secondLayer = layers!![firstIndex + 1]
		val resultBounds = firstLayer.bounds
		resultBounds.union(secondLayer.bounds)
		val matrix = Matrix()
		matrix.preTranslate(-resultBounds.left.toFloat(), -resultBounds.top.toFloat())
		var resultBitmap = Bitmap.createBitmap(resultBounds.width(), resultBounds.height(), Bitmap.Config.ARGB_8888)
		val resultCanvas = Canvas(resultBitmap!!)
		resultBitmap = secondLayer.drawLayerAndReturnBitmap(resultBitmap, resultCanvas, null, matrix)
		resultBitmap = firstLayer.drawLayerAndReturnBitmap(resultBitmap, resultCanvas, null, matrix)
		val resultLayer = Layer(resultBounds.left, resultBounds.top, firstLayer.name, resultBounds.width(), resultBounds.height(), Color.TRANSPARENT)
		resultLayer.setBitmap(resultBitmap)
		image!!.deleteLayer(firstLayer)
		image!!.deleteLayer(secondLayer)
		image!!.addLayer(resultLayer, firstIndex)
		createJoinHistoryAction(firstLayer, secondLayer, firstIndex)
	}

	private fun createJoinHistoryAction(firstLayer: Layer, secondLayer: Layer, resultLayerId: Int)
	{
		val action = ActionLayerJoin(image)
		action.setLayers(firstLayer, secondLayer, resultLayerId)
		action.applyAction()
	}*/
}
