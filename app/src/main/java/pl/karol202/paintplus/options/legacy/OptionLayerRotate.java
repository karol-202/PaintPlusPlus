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

package pl.karol202.paintplus.options.legacy;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionLayerRotate;
import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.util.MathUtils;

public class OptionLayerRotate extends LegacyOption implements DialogInterface.OnClickListener, SeekBar.OnSeekBarChangeListener
{
	private AlertDialog dialog;
	private SeekBar seekBarAngle;
	private TextView textAngle;

	private Layer layer;

	public OptionLayerRotate(AppContextLegacy context, LegacyImage image)
	{
		super(context, image);
		layer = image.getSelectedLayer();
	}

	@Override
	@SuppressLint("InflateParams")
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.dialog_rotate_layer, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setTitle(R.string.dialog_rotate_layer);
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, null);

		seekBarAngle = view.findViewById(R.id.seekBar_angle);
		seekBarAngle.setProgress(angleToProgress(0));
		seekBarAngle.setOnSeekBarChangeListener(this);

		textAngle = view.findViewById(R.id.text_angle);
		setAngleText(0);

		dialog = dialogBuilder.create();
		dialog.show();
	}

	private void setAngleText(int angle)
	{
		textAngle.setText(getContext().getResources().getString(R.string.angle, angle));
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		float angle = progressToAngle(seekBarAngle.getProgress());
		rotate(angle);
		getImage().updateImage();
	}

	private void rotate(float angle)
	{
		ActionLayerRotate action = new ActionLayerRotate(getImage());
		action.setLayerBeforeRotation(layer);

		layer.rotate(angle);

		action.applyAction();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		setAngleText(Math.round(progressToAngle(progress)));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }

	private int angleToProgress(float angle)
	{
		return (int) MathUtils.map(angle, -180, 180, 0, seekBarAngle.getMax());
	}

	private float progressToAngle(int progress)
	{
		return MathUtils.map(progress, 0, seekBarAngle.getMax(), -180, 180);
	}
}
