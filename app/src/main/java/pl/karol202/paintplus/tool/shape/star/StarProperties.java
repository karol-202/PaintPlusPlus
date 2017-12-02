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

package pl.karol202.paintplus.tool.shape.star;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.shape.Join;
import pl.karol202.paintplus.tool.shape.JoinAdapter;
import pl.karol202.paintplus.tool.shape.ShapeProperties;
import pl.karol202.paintplus.util.SeekBarTouchListener;

public class StarProperties extends ShapeProperties
{
	private final int MIN_CORNERS = 3;
	private final int MAX_CORNERS = 20;
	
	private ShapeStar star;
	private String errorToFew;
	private String errorToMany;
	private JoinAdapter adapter;
	
	private View view;
	private ImageButton buttonMinusCorners;
	private ImageButton buttonPlusCorners;
	private TextInputLayout editLayoutCorners;
	private EditText editCorners;
	private CheckBox checkFill;
	private SeekBar seekBarWidth;
	private TextView textWidth;
	private Spinner spinnerJoin;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_star, container, false);
		
		star = (ShapeStar) shape;
		errorToFew = getActivity().getString(R.string.error_polygon_too_few_sides);
		errorToMany = getActivity().getString(R.string.error_polygon_too_many_sides);
		adapter = new JoinAdapter(getActivity());
		
		buttonMinusCorners = view.findViewById(R.id.button_minus_star_corners);
		buttonMinusCorners.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if(getSides() > MIN_CORNERS) editCorners.setText(String.valueOf(getSides() - 1));
			}
		});
		
		buttonPlusCorners = view.findViewById(R.id.button_plus_star_corners);
		buttonPlusCorners.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if(getSides() < MAX_CORNERS) editCorners.setText(String.valueOf(getSides() + 1));
			}
		});
		
		editLayoutCorners = view.findViewById(R.id.edit_layout_star_corners);
		editLayoutCorners.setHintEnabled(false);
		
		editCorners = editLayoutCorners.getEditText();
		if(editCorners == null) throw new RuntimeException("TextInputLayout must contain EditText.");
		editCorners.setText(String.valueOf(star.getCorners()));
		editCorners.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			
			@Override
			public void afterTextChanged(Editable s)
			{
				int sides = getSides();
				if(sides < MIN_CORNERS) editLayoutCorners.setError(errorToFew);
				else if(sides > MAX_CORNERS) editLayoutCorners.setError(errorToMany);
				else
				{
					editLayoutCorners.setErrorEnabled(false);
					star.setCorners(getSides());
				}
			}
		});
		
		checkFill = view.findViewById(R.id.check_star_fill);
		checkFill.setChecked(star.isFill());
		checkFill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				star.setFill(isChecked);
			}
		});
		
		seekBarWidth = view.findViewById(R.id.seek_star_width);
		seekBarWidth.setProgress(star.getLineWidth() - 1);
		seekBarWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				star.setLineWidth(progress + 1);
				textWidth.setText(String.valueOf(progress + 1));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
		});
		seekBarWidth.setOnTouchListener(new SeekBarTouchListener());
		
		textWidth = view.findViewById(R.id.star_width);
		textWidth.setText(String.valueOf(star.getLineWidth()));
		
		spinnerJoin = view.findViewById(R.id.spinner_star_join);
		spinnerJoin.setAdapter(adapter);
		spinnerJoin.setSelection(star.getJoin().ordinal());
		spinnerJoin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				Join join = adapter.getItem(position);
				star.setJoin(join);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});
		
		return view;
	}
	
	private int getSides()
	{
		if(editCorners.getText().length() == 0) return 0;
		return Integer.parseInt(editCorners.getText().toString());
	}
}