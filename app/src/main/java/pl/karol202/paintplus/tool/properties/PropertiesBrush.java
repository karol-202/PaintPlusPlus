package pl.karol202.paintplus.tool.properties;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.Tool.OnToolUpdatedListener;
import pl.karol202.paintplus.tool.ToolBrush;

public class PropertiesBrush extends Fragment implements SeekBar.OnSeekBarChangeListener
{
	private ToolBrush brush;
	private OnToolUpdatedListener listener;

	private SeekBar seekBrushSize;
	private TextView textBrushSize;
	private SeekBar seekBrushShapeOffset;
	private TextView textBrushShapeOffset;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		try
		{
			listener = (OnToolUpdatedListener) getActivity();
		}
		catch(ClassCastException e)
		{
			throw new ClassCastException(getActivity().toString() + "must implement OnToolUpdatedListener.");
		}

		Bundle bundle = getArguments();
		if(bundle != null) this.brush = bundle.getParcelable("tool");

		View view = inflater.inflate(R.layout.properties_brush, container, false);
		seekBrushSize = (SeekBar) view.findViewById(R.id.seekBar_brush_size);
		seekBrushSize.setProgress((int) (brush.getSize() - 1));
		seekBrushSize.setOnSeekBarChangeListener(this);

		textBrushSize = (TextView) view.findViewById(R.id.brush_size);
		textBrushSize.setText(Float.toString(brush.getSize()));

		seekBrushShapeOffset = (SeekBar) view.findViewById(R.id.seek_brush_shape_offset);
		seekBrushShapeOffset.setProgress((int) brush.getShapeOffset());
		seekBrushShapeOffset.setOnSeekBarChangeListener(this);

		textBrushShapeOffset = (TextView) view.findViewById(R.id.brush_shape_offset);
		textBrushShapeOffset.setText(Float.toString(brush.getShapeOffset()));

		return view;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		if(seekBar == seekBrushSize)
		{
			brush.setSize((progress + 1));
			textBrushSize.setText(Integer.toString(progress + 1));
			listener.onToolUpdated(brush);
		}
		else if(seekBar == seekBrushShapeOffset)
		{
			brush.setShapeOffset(progress);
			textBrushShapeOffset.setText(Integer.toString(progress));
			listener.onToolUpdated(brush);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
}
