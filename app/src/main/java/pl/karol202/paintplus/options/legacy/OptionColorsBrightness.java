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
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.manipulators.ColorsBrightness;
import pl.karol202.paintplus.color.manipulators.params.BrightnessParams;
import pl.karol202.paintplus.color.manipulators.params.ManipulatorSelection;
import pl.karol202.paintplus.history.action.ActionLayerChange;
import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.selection.Selection;

import java.util.Locale;

public class OptionColorsBrightness extends LegacyOption implements DialogInterface.OnClickListener, SeekBar.OnSeekBarChangeListener,
                                                                    View.OnTouchListener
{
	private ColorsBrightness manipulator;
	private Layer layer;
	private Bitmap oldBitmap;

	private AlertDialog dialog;
	private SeekBar seekBarBrightness;
	private SeekBar seekBarContrast;
	private TextView textBrightness;
	private TextView textContrast;
	private Button buttonPreview;

	public OptionColorsBrightness(AppContextLegacy context, LegacyImage image)
	{
		super(context, image);
		this.manipulator = new ColorsBrightness();
		this.layer = image.getSelectedLayer();
		this.oldBitmap = Bitmap.createBitmap(layer.getBitmap());
	}

	@Override
	@SuppressLint("InflateParams")
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.dialog_colors_brightness, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setTitle(R.string.dialog_colors_brightness);
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, this);

		seekBarBrightness = view.findViewById(R.id.seekBar_brightness);
		seekBarBrightness.setOnSeekBarChangeListener(this);

		seekBarContrast = view.findViewById(R.id.seekBar_contrast);
		seekBarContrast.setOnSeekBarChangeListener(this);

		textBrightness = view.findViewById(R.id.brightness);
		textBrightness.setText(getText(seekBarBrightness));

		textContrast = view.findViewById(R.id.contrast);
		textContrast.setText(getText(seekBarContrast));

		buttonPreview = view.findViewById(R.id.button_preview);
		buttonPreview.setOnTouchListener(this);

		dialog = dialogBuilder.create();
		dialog.show();
	}

	private String getText(SeekBar seekBar)
	{
		return String.format(Locale.US, "%1$d%%", getValue(seekBar));
	}

	private int getValue(SeekBar seekBar)
	{
		return seekBar.getProgress() - 100;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		if(seekBar == seekBarBrightness) textBrightness.setText(getText(seekBarBrightness));
		else if(seekBar == seekBarContrast) textContrast.setText(getText(seekBarContrast));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if(which == DialogInterface.BUTTON_POSITIVE) applyChanges(true);
		else if(which == DialogInterface.BUTTON_NEGATIVE) revertChanges();
	}

	private void applyChanges(boolean applyToHistory)
	{
		ActionLayerChange action = new ActionLayerChange(getImage(), R.string.history_action_brightness);
		action.setLayerChange(getImage().getLayerIndex(layer), layer.getBitmap());

		Selection selection = getImage().getSelection();
		ManipulatorSelection manipulatorSelection = ManipulatorSelection.fromSelection(selection, layer.getBounds());

		BrightnessParams params = new BrightnessParams(manipulatorSelection);
		params.setBrightness(getValue(seekBarBrightness) / 100f);
		params.setContrast(getValue(seekBarContrast) / 100f);

		Bitmap bitmapOut = manipulator.run(oldBitmap, params);
		layer.setBitmap(bitmapOut);

		if(applyToHistory) action.applyAction();
	}

	private void revertChanges()
	{
		layer.setBitmap(oldBitmap);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			applyChanges(false);
			dialog.hide();
			v.getParent().requestDisallowInterceptTouchEvent(true);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			revertChanges();
			dialog.show();
			v.getParent().requestDisallowInterceptTouchEvent(false);
		}
		return true;
	}
}
