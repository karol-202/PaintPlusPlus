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

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.ItemLayerBinding
import pl.karol202.paintplus.util.MathUtils.dpToPixels
import pl.karol202.paintplus.util.setAnimationEndListener
import kotlin.properties.Delegates

@SuppressLint("ClickableViewAccessibility")
class LayerViewHolder(private val views: ItemLayerBinding,
                      private val layerHandle: LayerHandle,
                      private val onInfoShow: (Layer) -> Unit,
                      private val onPropertiesEdit: (Layer) -> Unit,
                      private val onNameChange: (Layer) -> Unit,
                      private val onSelect: (Layer) -> Unit,
                      private val onVisibilityToggle: (Layer) -> Unit,
                      private val onDuplicate: (Layer) -> Unit,
                      private val onMerge: (Layer) -> Unit,
                      private val onDelete: (Layer) -> Unit) :
		RecyclerView.ViewHolder(views.root)
{
	companion object
	{
		const val HEIGHT_DP = 64f
		private const val RAISED_ELEVATION_DP = 18f
	}

	private data class BoundLayer(val layer: Layer,
	                              val isLast: Boolean,
	                              val areLayersLocked: Boolean)

	private val context = views.root.context

	private val elevationPx = dpToPixels(context, RAISED_ELEVATION_DP)
	private val animationDuration = context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

	val rootView get() = views.root

	private var boundLayer by Delegates.notNull<BoundLayer>()
	private var ghost = false
	private var rippleDrawable: RippleDrawable? = null
	private var animationTargetX: Float
	private var animationTargetY: Float

	private val layer get() = boundLayer.layer
	private val isLast get() = boundLayer.isLast
	private val areLayersLocked get() = boundLayer.areLayersLocked

	init
	{
		views.root.setOnTouchListener { _, event -> onTouch(event) }
		views.root.setOnLongClickListener { showMenu(); true }
		animationTargetX = 0f
		animationTargetY = 0f

		views.imageLayerHandle.setOnTouchListener { _, event -> onHandleTouch(event) }

		views.buttonLayerVisibility.setOnClickListener { onVisibilityToggle(layer) }

		views.buttonLayerMenu.setOnClickListener { showMenu() }
	}

	private fun onTouch(event: MotionEvent): Boolean
	{
		if(ghost) return false
		if(event.action == MotionEvent.ACTION_DOWN) showRipple(event.x, event.y)
		else if(event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL)
		{
			onSelect(layer)
			hideRipple()
		}
		return true
	}

	fun bind(layer: Layer, isLast: Boolean, isSelected: Boolean, areLayersLocked: Boolean)
	{
		boundLayer = BoundLayer(layer, isLast, areLayersLocked)

		setViewOffset(0f, 0f)
		setViewBackground(selected = isSelected)
		views.root.visibility = View.VISIBLE
		views.textLayerName.text = layer.name
		views.imageLayerPreview.setImageBitmap(layer.bitmap)
		views.buttonLayerVisibility.contentDescription = context.getString(if(layer.visible) R.string.desc_layer_visible
		                                                                   else R.string.desc_layer_invisible)
		views.imageLayerHandle.setImageResource(if(isSelected) R.drawable.ic_drag_handle_white_24dp
		                                        else R.drawable.ic_drag_handle_black_24dp)
		views.textLayerName.setTextColor(if(isSelected) Color.WHITE
		                                 else Color.BLACK)
		views.buttonLayerVisibility.setImageResource(
				when
				{
					isSelected && layer.visible -> R.drawable.ic_visible_white_24dp
					isSelected -> R.drawable.ic_invisible_white_24dp
					layer.visible -> R.drawable.ic_visible_black_24dp
					else -> R.drawable.ic_invisible_black_24dp
				})
		views.buttonLayerMenu.setImageResource(if(isSelected) R.drawable.ic_menu_white_24dp
		                                       else R.drawable.ic_menu_black_24dp)
	}

	private fun setViewBackground(selected: Boolean)
	{
		val drawable = context.getDrawable(if(selected) R.drawable.layer_view_selected
		                                   else R.drawable.layer_view)
		views.root.background = drawable
		rippleDrawable = drawable as RippleDrawable?
	}

	// TODO Select
	/*{
		if(adapter.isLayerSelected(layer) || adapter.areLayersLocked()) return
		adapter.notifyItemChanged(adapter.image.selectedLayerIndex)
		adapter.image.selectLayer(layer)
		bind(layer)
	}*/

	private fun showRipple(x: Float, y: Float)
	{
		rippleDrawable?.setHotspot(x, y)
		rippleDrawable?.state = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
	}

	private fun hideRipple()
	{
		rippleDrawable?.state = intArrayOf(android.R.attr.state_enabled)
	}

	private fun onHandleTouch(event: MotionEvent): Boolean
	{
		val x = event.rawX
		val y = event.rawY
		if(!ghost && event.action == MotionEvent.ACTION_DOWN)
		{
			if(areLayersLocked) return false
			layerHandle.setLayer(layer, this)
			layerHandle.onTouchStart(x, y)
		}
		else if(ghost)
		{
			if(areLayersLocked)
			{
				layerHandle.onTouchCancel()
				return false
			}
			if(event.action == MotionEvent.ACTION_MOVE) layerHandle.onTouchMove(x, y)
			else if(event.action == MotionEvent.ACTION_UP) layerHandle.onTouchStop(x, y)
		}
		return true
	}

	/*private fun toggleVisibility()
	{
		val action = ActionLayerVisibilityChange(adapter.image)
		action.setLayerBeforeChange(layer)
		layer.setVisibility(!layer!!.visible)
		if(adapter.isLayerSelected(layer))
			views.buttonLayerVisibility.setImageResource(if(layer!!.visible) R.drawable.ic_visible_white_24dp
			                                             else R.drawable.ic_invisible_white_24dp)
		else views.buttonLayerVisibility.setImageResource(if(layer!!.visible) R.drawable.ic_visible_black_24dp
		                                            else R.drawable.ic_invisible_black_24dp)
		action.applyAction()
	}*/

	private fun showMenu() = PopupMenu(context, views.buttonLayerMenu).run {
		setOnMenuItemClickListener(this@LayerViewHolder::onMenuItemClick)
		inflate(R.menu.menu_layer)
		if(areLayersLocked) menu.forEach { it.isEnabled = false }
		menu.findItem(R.id.action_merge).isEnabled = !isLast
		show()
	}

	private fun onMenuItemClick(item: MenuItem): Boolean
	{
		when(item.itemId)
		{
			R.id.action_layer_info -> onInfoShow(layer) //
			R.id.action_layer_properties -> onPropertiesEdit(layer) //
			R.id.action_layer_change_name -> onNameChange(layer) // showNameDialog()
			R.id.action_layer_duplicate -> onDuplicate(layer) // adapter.duplicateLayer(layer)
			R.id.action_merge -> onMerge(layer) // adapter.joinWithNextLayer(layer)
			R.id.action_layer_delete -> onDelete(layer) // delete()
			else -> return false
		}
		return true
	}

	/*@SuppressLint("InflateParams")
	private fun showNameDialog()
	{
		val inflater = LayoutInflater.from(context)
		val dialogView = inflater.inflate(R.layout.dialog_layer_name, null, false)
		val builder = AlertDialog.Builder(context)
		builder.setView(dialogView)
		builder.setTitle(R.string.dialog_layer_name)
		val editTextName = dialogView.findViewById<EditText>(R.id.edit_layer_name)
		editTextName.setText(layer!!.name)
		builder.setPositiveButton(R.string.ok) { _, _ ->
			val action = ActionLayerNameChange(adapter.image)
			action.setLayer(layer)
			layer.setName(editTextName.text.toString())
			adapter.notifyDataSetChanged()
			action.applyAction()
		}
		builder.setNegativeButton(R.string.cancel, null)
		builder.show()
	}

	private fun delete()
	{
		val message = context.getString(R.string.dialog_layer_delete, layer!!.name)
		val builder = AlertDialog.Builder(context)
		builder.setMessage(message)
		builder.setPositiveButton(R.string.layer_delete) { _, _ ->
			val action = ActionLayerDelete(adapter.image)
			action.setLayerBeforeDeleting(layer)
			adapter.image.deleteLayer(layer)
			adapter.notifyDataSetChanged()
			action.applyAction()
		}
		builder.setNegativeButton(R.string.cancel, null)
		builder.show()
	}*/

	fun hide()
	{
		views.root.visibility = View.INVISIBLE
	}

	fun setViewOffset(x: Float, y: Float)
	{
		views.root.translationX = x
		views.root.translationY = y
	}

	fun setViewOffsetWithAnimation(x: Float, y: Float, listener: () -> Unit = {})
	{
		if(x == animationTargetX && y == animationTargetY) return
		animationTargetX = x
		animationTargetY = y
		views.root.animate().translationX(x).translationY(y).setDuration(animationDuration).setAnimationEndListener(listener).start()
	}

	private fun setElevation()
	{
		if(ghost) views.root.translationZ = elevationPx
	}

	fun setGhost()
	{
		ghost = true
		setElevation()
	}
}
