package pl.karol202.paintplus.tool.shape.line;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.shape.ShapeProperties;

public class LineProperties extends ShapeProperties implements SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener
{
	private ShapeLine line;
	private CapAdapter capAdapter;
	
	private View view;
	private SeekBar seekBarWidth;
	private TextView textWidth;
	private Spinner spinnerCap;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_line, container, false);
		
		line = (ShapeLine) shape;
		capAdapter = new CapAdapter(getActivity());
		
		seekBarWidth = (SeekBar) view.findViewById(R.id.seekBar_line_width);
		seekBarWidth.setProgress(line.getLineWidth() - 1);
		seekBarWidth.setOnSeekBarChangeListener(this);
		
		textWidth = (TextView) view.findViewById(R.id.line_width);
		textWidth.setText(String.valueOf(line.getLineWidth()));
		
		spinnerCap = (Spinner) view.findViewById(R.id.spinner_line_cap);
		spinnerCap.setAdapter(capAdapter);
		spinnerCap.setSelection(line.getLineCap().ordinal());
		spinnerCap.setOnItemSelectedListener(this);
		
		return view;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		line.setLineWidth(progress + 1);
		textWidth.setText(String.valueOf(progress + 1));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		Cap cap = capAdapter.getItem(position);
		line.setLineCap(cap);
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) { }
}