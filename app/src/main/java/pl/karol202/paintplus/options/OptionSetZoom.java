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

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.pan.PanProperties;

import java.util.Locale;

public class OptionSetZoom extends Option implements View.OnClickListener, TextWatcher
{
	private double zoom;
	private boolean dontFireEvent;
	
	private AlertDialog alertDialog;
	private ImageButton buttonZoomOut;
	private ImageButton buttonZoomIn;
	private EditText editTextZoom;
	
	public OptionSetZoom(Context context, Image image)
	{
		super(context, image);
	}
	
	@Override
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_set_zoom, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.dialog_set_zoom);
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, null);
		
		zoom = image.getZoom();
		
		buttonZoomOut = view.findViewById(R.id.button_zoom_out);
		buttonZoomOut.setOnClickListener(this);
		
		buttonZoomIn = view.findViewById(R.id.button_zoom_in);
		buttonZoomIn.setOnClickListener(this);
		
		editTextZoom = view.findViewById(R.id.edit_zoom);
		updateZoom(zoom, true);
		editTextZoom.addTextChangedListener(this);
		
		alertDialog = dialogBuilder.create();
		alertDialog.show();
	}
	
	@Override
	public void onClick(View view)
	{
		if(view == buttonZoomOut)
		{
			zoom = getLowerZoom();
			updateZoom(zoom, true);
		}
		else if(view == buttonZoomIn)
		{
			zoom = getGreaterZoom();
			updateZoom(zoom, true);
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) { }
	
	@Override
	public void afterTextChanged(Editable s)
	{
		if(dontFireEvent) return;
		String string = s.toString();
		if(!string.endsWith("%"))
		{
			updateZoom(zoom, true);
			editTextZoom.setSelection(editTextZoom.getText().length() - 1);
			return;
		}
		
		if(!string.equals("%")) updateZoom((float) textToZoom(string), false);
	}
	
	private void updateZoom(double zoom, boolean updateText)
	{
		dontFireEvent = true;
		
		if(zoom < PanProperties.MIN_ZOOM) updateZoom(PanProperties.MIN_ZOOM, true);
		else if(zoom > PanProperties.MAX_ZOOM) updateZoom(PanProperties.MAX_ZOOM, true);
		else
		{
			this.zoom = zoom;
			if(updateText) editTextZoom.setText(zoomToText(zoom));
			image.setZoom((float) zoom);
		}
		
		dontFireEvent = false;
	}
	
	private String zoomToText(double zoom)
	{
		return String.format(Locale.US, "%d%%", Math.round(zoom * 100));
	}
	
	private double textToZoom(String text)
	{
		return Integer.parseInt(text.substring(0, text.length() - 1)) / 100d;
	}
	
	private double getLowerZoom()
	{
		int position = 0;
		if(zoom > 1)
		{
			double lower = 1;
			while(true)
			{
				double next = calculateZoomRatio(++position);
				if(next >= zoom) return lower;
				lower = next;
			}
		}
		else
		{
			while(true)
			{
				double next = calculateZoomRatio(--position);
				if(next < zoom) return next;
			}
		}
	}
	
	private double getGreaterZoom()
	{
		int position = 0;
		if(zoom < 1)
		{
			double greater = 1;
			while(true)
			{
				double next = calculateZoomRatio(--position);
				if(next <= zoom) return greater;
				greater = next;
			}
		}
		else
		{
			while(true)
			{
				double next = calculateZoomRatio(++position);
				if(next > zoom) return next;
			}
		}
	}
	
	private double calculateZoomRatio(int position)
	{
		int posAbs = Math.abs(position);
		double fract = Math.pow(PanProperties.SQRT2, posAbs);
		double round = Math.round(fract * 2) / 2f;
		if(position >= 0) return round;
		else return 1 / round;
	}
}