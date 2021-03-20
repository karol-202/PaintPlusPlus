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

package pl.karol202.paintplus.options;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionLayerDrag;
import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.image.layer.Layer;

public class OptionLayerDrag extends LegacyOption
{
	private EditText editX;
	private EditText editY;

	private Layer layer;

	public OptionLayerDrag(AppContextLegacy context, LegacyImage image)
	{
		super(context, image);
		layer = getImage().getSelectedLayer();
	}

	@SuppressLint("InflateParams")
	@Override
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.dialog_drag_layer, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.dialog_drag_layer);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, (dialog, which) -> offsetLayer());
		builder.setNegativeButton(R.string.cancel, null);

		editX = view.findViewById(R.id.edit_layer_offset_x);
		editX.setText(String.valueOf(layer.getX()));

		editY = view.findViewById(R.id.edit_layer_offset_y);
		editY.setText(String.valueOf(layer.getY()));

		builder.show();
	}

	private void offsetLayer()
	{
		int x = Integer.parseInt(editX.getText().toString());
		int y = Integer.parseInt(editY.getText().toString());

		ActionLayerDrag action = new ActionLayerDrag(getImage());
		action.setLayerAndDragDelta(layer, x - layer.getX(), y - layer.getY());

		layer.setPosition(x, y);

		action.applyAction();
	}
}
