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

import android.app.AlertDialog
import android.content.Context
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogLayerInfoBinding
import pl.karol202.paintplus.util.layoutInflater

class LayerInformationDialog(private val context: Context,
                             private val layer: Layer)
{
	fun show()
	{
		val views = DialogLayerInfoBinding.inflate(context.layoutInflater)

		val builder = AlertDialog.Builder(context)
		builder.setTitle(R.string.dialog_layer_info)
		builder.setView(views.root)
		builder.setPositiveButton(R.string.ok, null)

		views.textLayerInfoNameValue.text = layer.name
		views.textLayerInfoWidthValue.text = layer.width.toString()
		views.textLayerInfoHeightValue.text = layer.height.toString()
		views.textLayerInfoXValue.text = layer.x.toString()
		views.textLayerInfoYValue.text = layer.y.toString()
		views.textLayerInfoOpacityValue.text = context.getString(R.string.opacity, (layer.opacity * 100).toInt())
		views.textLayerInfoModeValue.setText(layer.mode.name)
		views.textLayerInfoVisibilityValue.setText(if(layer.visible) R.string.yes else R.string.no)
		builder.show()
	}
}
