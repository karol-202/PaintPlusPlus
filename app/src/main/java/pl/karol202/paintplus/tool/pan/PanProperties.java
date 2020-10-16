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

package pl.karol202.paintplus.tool.pan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.ToolProperties;

import java.util.Locale;

public class PanProperties extends ToolProperties implements View.OnClickListener, TextWatcher, ToolPan.OnZoomChangeListener
{
	public static final double SQRT2 = Math.sqrt(2);
	public static final double MIN_ZOOM = 0.009;
	public static final double MAX_ZOOM = 16;
	
	private ToolPan pan;
	private double zoom;
	private boolean dontFireEvent;
	
	private ImageButton buttonZoomOut;
	private ImageButton buttonZoomIn;
	private EditText editTextZoom;
	private Button buttonCenter;
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.properties_pan, container, false);
		pan = (ToolPan) tool;
		pan.setZoomListener(this);
		zoom = pan.getZoom();
		
		buttonZoomOut = view.findViewById(R.id.button_zoom_out);
		buttonZoomOut.setOnClickListener(this);
		
		buttonZoomIn = view.findViewById(R.id.button_zoom_in);
		buttonZoomIn.setOnClickListener(this);
		
		editTextZoom = view.findViewById(R.id.edit_zoom);
		updateZoom(zoom, true);
		editTextZoom.addTextChangedListener(this);
		
		buttonCenter = view.findViewById(R.id.button_center_view);
		buttonCenter.setOnClickListener(this);
		return view;
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
		else if(view == buttonCenter) pan.centerView();
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
		
		if(zoom < MIN_ZOOM) updateZoom(MIN_ZOOM, true);
		else if(zoom > MAX_ZOOM) updateZoom(MAX_ZOOM, true);
		else
		{
			this.zoom = zoom;
			if(updateText) editTextZoom.setText(zoomToText(zoom));
			pan.setZoom((float) zoom);
		}
		
		dontFireEvent = false;
	}
	
	private String zoomToText(double zoom)
	{
		return String.format(Locale.US, "%.1f%%", zoom * 100);
	}
	
	private double textToZoom(String text)
	{
		return Float.parseFloat(text.substring(0, text.length() - 1)) / 100;
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
		double fract = Math.pow(SQRT2, posAbs);
		double round = Math.round(fract * 2) / 2f;
		if(position >= 0) return round;
		else return 1 / round;
	}
	
	@Override
	public void onZoomChanged()
	{
		updateZoom(pan.getZoom(), true);
	}
}