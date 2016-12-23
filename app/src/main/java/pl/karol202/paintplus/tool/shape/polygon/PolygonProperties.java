package pl.karol202.paintplus.tool.shape.polygon;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
	private String errorToFew;
	private String errorToMany;
	
	private View view;
	private ImageButton buttonMinusSides;
	private ImageButton buttonPlusSides;
	private TextInputLayout editLayoutSides;
	private EditText editSides;
	private CheckBox checkFill;
	private SeekBar seekBarWidth;
	private TextView textWidth;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_polygon, container, false);
		
		polygon = (ShapePolygon) shape;
		errorToFew = getActivity().getString(R.string.error_polygon_too_few_sides);
		errorToMany = getActivity().getString(R.string.error_polygon_too_many_sides);
		
		buttonMinusSides = (ImageButton) view.findViewById(R.id.button_minus_polygon_sides);
		buttonMinusSides.setOnClickListener(this);
		
		buttonPlusSides = (ImageButton) view.findViewById(R.id.button_plus_polygon_sides);
		buttonPlusSides.setOnClickListener(this);
		
		editLayoutSides = (TextInputLayout) view.findViewById(R.id.edit_layout_polygon_sides);
		editLayoutSides.setHintEnabled(false);
		
		editSides = editLayoutSides.getEditText();
		if(editSides == null) throw new RuntimeException("TextInputLayout must contain EditText.");
		editSides.setText(String.valueOf(polygon.getSides()));
		editSides.addTextChangedListener(this);
		
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
			editSides.setText(String.valueOf(getSides() - 1));
		}
		else if(v == buttonPlusSides)
		{
			if(getSides() >= MAX_SIDES) return;
			editSides.setText(String.valueOf(getSides() + 1));
		}
	}
	
	private int getSides()
	{
		if(editSides.getText().length() == 0) return 0;
		return Integer.parseInt(editSides.getText().toString());
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) { }
	
	@Override
	public void afterTextChanged(Editable s)
	{
		int sides = getSides();
		if(sides < MIN_SIDES) editLayoutSides.setError(errorToFew);
		else if(sides > MAX_SIDES) editLayoutSides.setError(errorToMany);
		else
		{
			editLayoutSides.setErrorEnabled(false);
			polygon.setSides(getSides());
		}
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