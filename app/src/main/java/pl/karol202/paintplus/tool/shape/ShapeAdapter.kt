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
package pl.karol202.paintplus.tool.shape

import android.content.Context
import pl.karol202.paintplus.tool.shape.AbstractShape
import android.widget.ArrayAdapter
import pl.karol202.paintplus.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import pl.karol202.paintplus.databinding.SpinnerItemGradientRepeatabilityBinding
import pl.karol202.paintplus.databinding.SpinnerItemShapeBinding
import pl.karol202.paintplus.util.layoutInflater

internal class ShapeAdapter(context: Context,
                            shapes: List<Shape>) :
		ArrayAdapter<Shape>(context, R.layout.spinner_item_shape, shapes)
{
	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
	{
		val shape = getItem(position)!!
		val views = convertView?.let(SpinnerItemShapeBinding::bind)
				?: SpinnerItemShapeBinding.inflate(context.layoutInflater, parent, false)

		views.imageShapeIcon.setImageResource(shape.icon)
		views.textShapeName.setText(shape.name)
		return views.root
	}

	override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup) =
			getView(position, convertView, parent)
}
