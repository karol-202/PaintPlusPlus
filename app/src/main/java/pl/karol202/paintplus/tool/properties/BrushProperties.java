package pl.karol202.paintplus.tool.properties;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.ToolBrush;

public class BrushProperties extends ToolProperties implements SeekBar.OnSeekBarChangeListener
{
	private ToolBrush brush;
	
	private View view;
	private SeekBar seekBrushSize;
	private TextView textBrushSize;
	private SeekBar seekBrushShapeOffset;
	private TextView textBrushShapeOffset;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_brush, container, false);
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		brush = (ToolBrush) tool;
		
		seekBrushSize = (SeekBar) view.findViewById(R.id.seekBar_brush_size);
		seekBrushSize.setProgress((int) (brush.getSize() - 1));
		seekBrushSize.setOnSeekBarChangeListener(this);
		
		textBrushSize = (TextView) view.findViewById(R.id.brush_size);
		textBrushSize.setText(String.valueOf((int) brush.getSize()));
		
		seekBrushShapeOffset = (SeekBar) view.findViewById(R.id.seek_brush_shape_offset);
		seekBrushShapeOffset.setProgress((int) brush.getShapeOffset());
		seekBrushShapeOffset.setOnSeekBarChangeListener(this);
		
		textBrushShapeOffset = (TextView) view.findViewById(R.id.brush_shape_offset);
		textBrushShapeOffset.setText(String.valueOf((int) brush.getShapeOffset()));
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		if(seekBar == seekBrushSize)
		{
			brush.setSize((progress + 1));
			textBrushSize.setText(String.valueOf(progress + 1));
		}
		else if(seekBar == seekBrushShapeOffset)
		{
			brush.setShapeOffset(progress);
			textBrushShapeOffset.setText(String.valueOf(progress));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
}
