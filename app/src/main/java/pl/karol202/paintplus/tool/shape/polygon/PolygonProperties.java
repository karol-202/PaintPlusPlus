package pl.karol202.paintplus.tool.shape.polygon;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.shape.ShapeProperties;

public class PolygonProperties extends ShapeProperties
							   implements OnClickListener, TextWatcher, OnCheckedChangeListener, OnSeekBarChangeListener
{
	private final int MIN_SIDES = 3;
	private final int MAX_SIDES = 20;
	
	private ShapePolygon polygon;
	
	private View view;
	private ImageButton buttonMinusSides;
	private ImageButton buttonPlusSides;
	private EditText editTextSides;
	private CheckBox checkFill;
	private SeekBar seekBarWidth;
	private TextView textWidth;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_polygon, container, false);
		
		polygon = (ShapePolygon) shape;
		
		buttonMinusSides = (ImageButton) view.findViewById(R.id.button_minus_polygon_sides);
		buttonMinusSides.setOnClickListener(this);
		
		buttonPlusSides = (ImageButton) view.findViewById(R.id.button_plus_polygon_sides);
		buttonPlusSides.setOnClickListener(this);
		
		editTextSides = (EditText) view.findViewById(R.id.edit_polygon_sides);
		editTextSides.setText(String.valueOf(polygon.getSides()));
		editTextSides.addTextChangedListener(this);
		
		checkFill = (CheckBox) view.findViewById(R.id.check_polygon_fill);
		checkFill.setChecked(polygon.isFill());
		checkFill.setOnCheckedChangeListener(this);
		
		seekBarWidth = (SeekBar) view.findViewById(R.id.seek_polygon_width);
		seekBarWidth.setProgress(polygon.getLineWidth() - 1);
		seekBarWidth.setOnSeekBarChangeListener(this);
		
		textWidth = (TextView) view.findViewById(R.id.polygon_width);
		textWidth.setText(String.valueOf(polygon.getLineWidth()));
		
		return view;
	}
	
	@Override
	public void onClick(View v)
	{
		if(v == buttonMinusSides)
		{
			if(getSides() <= MIN_SIDES) return;
			editTextSides.setText(String.valueOf(getSides() - 1));
		}
		else if(v == buttonPlusSides)
		{
			if(getSides() >= MAX_SIDES) return;
			editTextSides.setText(String.valueOf(getSides() + 1));
		}
	}
	
	private int getSides()
	{
		return Integer.parseInt(editTextSides.getText().toString());
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) { }
	
	@Override
	public void afterTextChanged(Editable s)
	{
		polygon.setSides(getSides());
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		polygon.setFill(isChecked);
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		polygon.setLineWidth(progress + 1);
		textWidth.setText(String.valueOf(progress + 1));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
}