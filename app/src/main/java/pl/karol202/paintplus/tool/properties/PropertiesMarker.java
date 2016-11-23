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
import pl.karol202.paintplus.tool.ToolMarker;

public class PropertiesMarker extends Fragment
{
	private ToolMarker marker;
	private OnToolUpdatedListener listener;

	private SeekBar seekMarkerSize;
	private TextView textMarkerSize;

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
		if(bundle != null) this.marker = bundle.getParcelable("tool");

		View view = inflater.inflate(R.layout.properties_marker, container, false);
		seekMarkerSize = (SeekBar) view.findViewById(R.id.seekBar_marker_size);
		seekMarkerSize.setProgress((int) (marker.getSize() - 1));
		seekMarkerSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				textMarkerSize.setText(Integer.toString(progress + 1));
				marker.setSize((progress + 1));
				listener.onToolUpdated(marker);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
		});
		textMarkerSize = (TextView) view.findViewById(R.id.marker_size);
		textMarkerSize.setText(Integer.toString(seekMarkerSize.getProgress() + 1));
		return view;
	}
}
