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
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.AppContext;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.util.Utils;

public abstract class OptionRotate extends Option implements DialogInterface.OnClickListener, SeekBar.OnSeekBarChangeListener
{
	private AlertDialog dialog;
	
	private SeekBar seekBarAngle;
	private TextView textAngle;
	
	OptionRotate(AppContext context, Image image)
	{
		super(context, image);
	}
	
	@Override
	@SuppressLint("InflateParams")
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.dialog_rotate, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setTitle(getTitle());
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, null);
		
		seekBarAngle = view.findViewById(R.id.seekBar_angle);
		seekBarAngle.setProgress(angleToProgress(0));
		seekBarAngle.setOnSeekBarChangeListener(this);
		
		textAngle = view.findViewById(R.id.text_angle);
		textAngle.setText("0°");
		
		dialog = dialogBuilder.create();
		dialog.show();
	}
	
	protected abstract int getTitle();
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		float angle = progressToAngle(seekBarAngle.getProgress());
		rotate(angle);
		getImage().updateImage();
	}
	
	protected abstract void rotate(float angle);
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		String angleRes = getContext().getResources().getString(R.string.angle);
		textAngle.setText(angleRes + " " + progressToAngle(progress) + "°");
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
	
	private int angleToProgress(float angle)
	{
		return (int) Utils.map(angle, -180, 180, 0, seekBarAngle.getMax());
	}
	
	private float progressToAngle(int progress)
	{
		return Utils.map(progress, 0, seekBarAngle.getMax(), -180, 180);
	}
}