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

package pl.karol202.paintplus.image.layer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.layer.mode.LayerModeType;

class LayerInformationDialog
{
	private Context context;
	private Layer layer;

	LayerInformationDialog(Context context, Layer layer)
	{
		this.context = context;
		this.layer = layer;
	}

	@SuppressLint("InflateParams")
	void show()
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_layer_info, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.dialog_layer_info);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, null);

		TextView textName = view.findViewById(R.id.text_layer_info_name_value);
		textName.setText(String.valueOf(layer.getName()));

		TextView textWidth = view.findViewById(R.id.text_layer_info_width_value);
		textWidth.setText(String.valueOf(layer.getWidth()));

		TextView textHeight = view.findViewById(R.id.text_layer_info_height_value);
		textHeight.setText(String.valueOf(layer.getHeight()));

		TextView textX = view.findViewById(R.id.text_layer_info_x_value);
		textX.setText(String.valueOf(layer.getX()));

		TextView textY = view.findViewById(R.id.text_layer_info_y_value);
		textY.setText(String.valueOf(layer.getY()));

		TextView textOpacity = view.findViewById(R.id.text_layer_info_opacity_value);
		textOpacity.setText(context.getString(R.string.opacity, (int) (layer.getOpacity() * 100)));

		TextView textMode = view.findViewById(R.id.text_layer_info_mode_value);
		LayerModeType type = LayerModeType.getTypeOfMode(layer.getMode());
		if(type != null) textMode.setText(type.getDisplayName());

		TextView textVisibility = view.findViewById(R.id.text_layer_info_visibility_value);
		textVisibility.setText(layer.isVisible() ? R.string.yes : R.string.no);

		builder.show();
	}
}
