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
package pl.karol202.paintplus.image.layer.mode

import android.content.Context
import android.widget.ArrayAdapter
import pl.karol202.paintplus.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.res.ResourcesCompat
import android.widget.TextView
import pl.karol202.paintplus.databinding.SpinnerItemGradientRepeatabilityBinding
import pl.karol202.paintplus.databinding.SpinnerItemLayerModeBinding
import pl.karol202.paintplus.util.layoutInflater

class LayerModeAdapter(context: Context) :
		ArrayAdapter<LayerModeType?>(context, R.layout.spinner_item_layer_mode, LayerModeType.values())
{
	override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
			getItemView(position, convertView, parent, false)

	override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup) =
			getItemView(position, convertView, parent, true)

	private fun getItemView(position: Int, convertView: View?, parent: ViewGroup, dropdown: Boolean): View
	{
		val mode = getItem(position)!!
		val views = convertView?.let(SpinnerItemLayerModeBinding::bind)
				?: SpinnerItemLayerModeBinding.inflate(context.layoutInflater, parent, false)

		val backgroundColor = ResourcesCompat.getColor(context.resources, R.color.layer_mode_item_background, null)
		if(dropdown) views.root.setBackgroundColor(backgroundColor)

		views.textLayerModeName.setText(mode.displayName)
		views.dividerLayerMode.visibility = if(dropdown && doesShowDivider(position, mode)) View.VISIBLE else View.GONE

		return views.root
	}

	private fun doesShowDivider(position: Int, mode: LayerModeType) =
			position != count - 1 && getItem(position + 1)?.category != mode.category
}
