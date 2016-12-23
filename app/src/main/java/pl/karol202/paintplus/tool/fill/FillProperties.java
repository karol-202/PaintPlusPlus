package pl.karol202.paintplus.tool.fill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.util.SeekBarTouchListener;

public class FillProperties extends ToolProperties implements SeekBar.OnSeekBarChangeListener
{
	private ToolFill fill;
	
	private View view;
	private SeekBar seekBarThreshold;
	private TextView textThreshold;
	private TextView textWarning;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_fill, container, false);
		fill = (ToolFill) tool;
		
		seekBarThreshold = (SeekBar) view.findViewById(R.id.seekBar_fill_threshold);
		seekBarThreshold.setProgress((int) fill.getFillThreshold());
		seekBarThreshold.setOnSeekBarChangeListener(this);
		seekBarThreshold.setOnTouchListener(new SeekBarTouchListener());
		
		textThreshold = (TextView) view.findViewById(R.id.fill_threshold);
		textThreshold.setText(String.valueOf((int) fill.getFillThreshold()) + "%");
		
		textWarning = (TextView) view.findViewById(R.id.text_fill_warning);
		return view;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		fill.setFillThreshold(progress);
		textThreshold.setText(String.valueOf(progress) + "%");
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
}