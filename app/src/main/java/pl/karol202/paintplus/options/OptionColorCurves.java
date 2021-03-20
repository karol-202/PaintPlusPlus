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
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.curves.ColorChannel;
import pl.karol202.paintplus.color.curves.ColorChannel.ColorChannelType;
import pl.karol202.paintplus.color.curves.ColorChannelsAdapter;
import pl.karol202.paintplus.color.curves.ColorCurvesView;
import pl.karol202.paintplus.color.curves.OnCurveEditListener;
import pl.karol202.paintplus.color.manipulators.ColorsCurveManipulator;
import pl.karol202.paintplus.color.manipulators.params.CurveManipulatorParams;
import pl.karol202.paintplus.color.manipulators.params.ManipulatorSelection;
import pl.karol202.paintplus.history.action.ActionLayerChange;
import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.selection.Selection;
import pl.karol202.paintplus.util.SeekBarTouchListener;

import static pl.karol202.paintplus.color.curves.ColorChannel.ColorChannelType.HSV;
import static pl.karol202.paintplus.color.curves.ColorChannel.ColorChannelType.RGB;

public class OptionColorCurves extends LegacyOption implements OnClickListener, AdapterView.OnItemSelectedListener,
                                                               View.OnTouchListener, View.OnClickListener, OnCurveEditListener
{
	private ColorChannelType channelType;
	private ColorChannelsAdapter adapterIn;
	private ColorChannelsAdapter adapterOut;
	private Layer layer;
	private Bitmap oldBitmap;
	private ColorsCurveManipulator manipulator;

	private AlertDialog alertDialog;
	private Spinner spinnerChannelIn;
	private Spinner spinnerChannelOut;
	private ColorCurvesView curvesView;
	private TextView textPoint;
	private Button buttonPreview;
	private Button buttonRestore;

	public OptionColorCurves(AppContextLegacy context, LegacyImage image, ColorChannelType type)
	{
		super(context, image);
		this.channelType = type;
		this.layer = image.getSelectedLayer();
		this.oldBitmap = Bitmap.createBitmap(layer.getBitmap());
		this.manipulator = new ColorsCurveManipulator();
	}

	@SuppressLint("InflateParams")
	@Override
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.dialog_color_curves, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(channelType == RGB ? R.string.dialog_color_curves_rgb : R.string.dialog_color_curves_hsv);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, this);
		builder.setNegativeButton(R.string.cancel, this);

		adapterIn = new ColorChannelsAdapter(getContext(), channelType);
		adapterOut = new ColorChannelsAdapter(getContext(), channelType);

		spinnerChannelIn = view.findViewById(R.id.spinner_curves_channel_in);
		spinnerChannelIn.setAdapter(adapterIn);
		spinnerChannelIn.setOnItemSelectedListener(this);
		if(channelType == RGB) spinnerChannelIn.setSelection(0);
		else if(channelType == HSV) spinnerChannelIn.setSelection(2);

		spinnerChannelOut = view.findViewById(R.id.spinner_curves_channel_out);
		spinnerChannelOut.setAdapter(adapterOut);
		spinnerChannelOut.setOnItemSelectedListener(this);
		if(channelType == RGB) spinnerChannelOut.setSelection(0);
		else if(channelType == HSV) spinnerChannelOut.setSelection(2);

		curvesView = view.findViewById(R.id.color_curves_view);
		curvesView.setOnTouchListener(new SeekBarTouchListener());
		curvesView.setOnCurveEditListener(this);
		curvesView.setChannelType(channelType);
		curvesView.setChannelIn((ColorChannel) spinnerChannelIn.getSelectedItem());
		curvesView.setChannelOut((ColorChannel) spinnerChannelOut.getSelectedItem());

		textPoint = view.findViewById(R.id.text_curve_point);
		textPoint.setText(curvesView.getInfoText());

		buttonPreview = view.findViewById(R.id.button_curves_preview);
		buttonPreview.setOnTouchListener(this);

		buttonRestore = view.findViewById(R.id.button_curves_restore);
		buttonRestore.setOnClickListener(this);

		alertDialog = builder.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if(which == DialogInterface.BUTTON_POSITIVE) applyChanges(true);
		else if(which == DialogInterface.BUTTON_NEGATIVE) revertChanges();
	}

	private void applyChanges(boolean applyToHistory)
	{
		ActionLayerChange action = new ActionLayerChange(getImage(), R.string.history_action_color_curves);
		action.setLayerChange(getImage().getLayerIndex(layer), layer.getBitmap());

		Selection selection = getImage().getSelection();
		ManipulatorSelection manipulatorSelection = ManipulatorSelection.fromSelection(selection, layer.getBounds());

		CurveManipulatorParams params = new CurveManipulatorParams(manipulatorSelection, channelType);
		curvesView.attachCurvesToParamsObject(params);

		Bitmap bitmapOut = manipulator.run(oldBitmap, params);
		layer.setBitmap(bitmapOut);

		if(applyToHistory) action.applyAction();
	}

	private void revertChanges()
	{
		layer.setBitmap(oldBitmap);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		if(parent == spinnerChannelIn) curvesView.setChannelIn((ColorChannel) spinnerChannelIn.getSelectedItem());
		else if(parent == spinnerChannelOut) curvesView.setChannelOut((ColorChannel) spinnerChannelOut.getSelectedItem());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) { }

	//Preview
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			applyChanges(false);
			alertDialog.hide();
			v.getParent().requestDisallowInterceptTouchEvent(true);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			revertChanges();
			alertDialog.show();
			v.getParent().requestDisallowInterceptTouchEvent(false);
		}
		return true;
	}

	//Restore
	@Override
	public void onClick(View v)
	{
		curvesView.restoreCurrentCurve();
	}

	@Override
	public void onCurveEdited()
	{
		textPoint.setText(curvesView.getInfoText());
	}
}
