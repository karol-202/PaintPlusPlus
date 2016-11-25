package pl.karol202.paintplus.tool.properties;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.ToolMarker;

public class MarkerProperties extends ToolProperties implements SeekBar.OnSeekBarChangeListener
{
	private ToolMarker marker;

	private SeekBar seekMarkerSize;
	private TextView textMarkerSize;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		marker = (ToolMarker) tool;

		View view = inflater.inflate(R.layout.properties_marker, container, false);
		seekMarkerSize = (SeekBar) view.findViewById(R.id.seekBar_marker_size);
		seekMarkerSize.setProgress((int) (marker.getSize() - 1));
		seekMarkerSize.setOnSeekBarChangeListener(this);
		textMarkerSize = (TextView) view.findViewById(R.id.marker_size);
		textMarkerSize.setText(String.valueOf(seekMarkerSize.getProgress() + 1));
		return view;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		textMarkerSize.setText(String.valueOf(progress + 1));
		marker.setSize((progress + 1));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
}
