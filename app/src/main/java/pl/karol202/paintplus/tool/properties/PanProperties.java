package pl.karol202.paintplus.tool.properties;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.ToolPan;

import java.util.Locale;

public class PanProperties extends ToolProperties implements View.OnClickListener, TextWatcher
{
	private final double SQRT2 = Math.sqrt(2);
	private final double MIN_ZOOM = 0.05;
	private final double MAX_ZOOM = 8;
	
	private ToolPan pan;
	private double zoom;
	private boolean dontFireEvent;
	
	private View view;
	private ImageButton buttonZoomOut;
	private ImageButton buttonZoomIn;
	private EditText editTextZoom;
	private Button buttonCenter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_pan, container, false);
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		pan = (ToolPan) tool;
		zoom = pan.getZoom();
		
		buttonZoomOut = (ImageButton) view.findViewById(R.id.button_zoom_out);
		buttonZoomOut.setOnClickListener(this);
		
		buttonZoomIn = (ImageButton) view.findViewById(R.id.button_zoom_in);
		buttonZoomIn.setOnClickListener(this);
		
		editTextZoom = (EditText) view.findViewById(R.id.edit_zoom);
		updateZoom(zoom);
		editTextZoom.addTextChangedListener(this);
		
		buttonCenter = (Button) view.findViewById(R.id.button_center_view);
		buttonCenter.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view)
	{
		if(view == buttonZoomOut)
		{
			zoom = getLowerZoom();
			updateZoom(zoom);
		}
		else if(view == buttonZoomIn)
		{
			zoom = getGreaterZoom();
			updateZoom(zoom);
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
			updateZoom(zoom);
			return;
		}
		
		if(!string.equals("%")) updateZoom((float) textToZoom(string));
	}
	
	private void updateZoom(double zoom)
	{
		dontFireEvent = true;
		
		if(zoom < MIN_ZOOM) updateZoom(MIN_ZOOM);
		else if(zoom > MAX_ZOOM) updateZoom(MAX_ZOOM);
		else
		{
			this.zoom = zoom;
			editTextZoom.setText(zoomToText(zoom));
			pan.setZoom((float) zoom);
		}
		
		dontFireEvent = false;
	}
	
	private String zoomToText(double zoom)
	{
		return String.format(Locale.US, "%.2f", zoom * 100) + "%";
	}
	
	private double textToZoom(String text)
	{
		return Double.parseDouble(text.substring(0, text.length() - 1)) / 100;
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
}