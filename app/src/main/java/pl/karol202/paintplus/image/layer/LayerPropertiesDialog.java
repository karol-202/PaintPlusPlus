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
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionLayerPropertiesChange;
import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.image.layer.mode.LayerModeAdapter;
import pl.karol202.paintplus.image.layer.mode.LayerModeType;

import java.util.Locale;

class LayerPropertiesDialog implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener, DialogInterface.OnClickListener
{
	private Context context;
	private LegacyImage image;
	private Layer layer;
	private LayerModeAdapter adapter;

	private LegacyLayerMode layerMode;
	private float opacity;

	private AlertDialog dialog;
	private Spinner spinnerMode;
	private SeekBar seekBarOpacity;
	private TextView textOpacity;

	LayerPropertiesDialog(Context context, LegacyImage image, Layer layer)
	{
		this.context = context;
		this.image = image;
		this.layer = layer;

		layerMode = layer.getMode();
		opacity = layer.getOpacity();

		init();
	}

	@SuppressLint("InflateParams")
	private void init()
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_layer_properties, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.dialog_layer_properties);
		builder.setPositiveButton(R.string.ok, this);
		builder.setNegativeButton(R.string.cancel, null);
		builder.setView(view);

		adapter = new LayerModeAdapter(context);

		spinnerMode = view.findViewById(R.id.spinner_layer_mode);
		spinnerMode.setAdapter(adapter);
		spinnerMode.setSelection(indexOf(layer.getMode()));
		spinnerMode.setOnItemSelectedListener(this);

		seekBarOpacity = view.findViewById(R.id.seekBar_layer_opacity);
		seekBarOpacity.setProgress((int) (layer.getOpacity() * 100));
		seekBarOpacity.setOnSeekBarChangeListener(this);

		textOpacity = view.findViewById(R.id.layer_opacity);
		textOpacity.setText(String.format(Locale.US, "%1$d%%", seekBarOpacity.getProgress()));

		dialog = builder.create();
	}

	private int indexOf(LegacyLayerMode mode)
	{
		return LayerModeType.getIndexOfMode(mode);
	}

	public void show()
	{
		dialog.show();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		try
		{
			LayerModeType type = adapter.getItem(position);
			layerMode = type.getLayerModeClass().newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) { }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		opacity = progress / 100f;
		textOpacity.setText(context.getString(R.string.opacity, seekBarOpacity.getProgress()));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		ActionLayerPropertiesChange action = new ActionLayerPropertiesChange(image);
		action.setLayerBeforeChange(layer);

		if(layerMode.getClass() != layer.getMode().getClass()) layer.setMode(layerMode);
		layer.setOpacity(opacity);

		action.applyAction();
	}
}
