package pl.karol202.paintplus.color.picker.panel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.picker.ColorPickerFragment;
import pl.karol202.paintplus.util.ColorPreviewView;

public class ColorPanelFragment extends ColorPickerFragment
{
	private ColorModeRGB modeRGB;
	private ColorModeHSV modeHSV;
	private ColorMode currentMode;
	
	private ColorChannelsAdapter channelsAdapter;
	
	private View view;
	private ColorPreviewView colorView;
	private ColorPickerSquarePanel squarePanel;
	private ColorPickerBar bar;
	private Spinner spinnerChannel;
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		modeRGB = new ColorModeRGB();
		modeHSV = new ColorModeHSV();
		
		channelsAdapter = new ColorChannelsAdapter(getContext());
		
		view = inflater.inflate(R.layout.color_picker_panel, container, false);
		
		colorView = view.findViewById(R.id.view_colors_panel_color);
		colorView.setColor(getCurrentColor());
		
		squarePanel = view.findViewById(R.id.color_picker_square_panel);
		
		bar = view.findViewById(R.id.color_picker_bar);
		
		spinnerChannel = view.findViewById(R.id.spinner_color_picker_channel);
		spinnerChannel.setAdapter(channelsAdapter);
		spinnerChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
			{
				setMainChannel(currentMode.getChannels()[position]);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) { }
		});
		
		setMode(modeRGB);
		return view;
	}
	
	@Override
	protected boolean onColorModeSelected(int actionId)
	{
		switch(actionId)
		{
		case R.id.mode_rgb: setMode(modeRGB); break;
		case R.id.mode_hsv: setMode(modeHSV); break;
		default: return false;
		}
		return true;
	}
	
	@Override
	protected boolean isColorModeSupported(int actionId)
	{
		return actionId == R.id.mode_rgb ||
				actionId == R.id.mode_hsv;
	}
	
	private void setMode(ColorMode mode)
	{
		this.currentMode = mode;
		ColorChannel mainChannel = currentMode.getChannels()[0];
		
		squarePanel.setModeAndMainChannel(currentMode, mainChannel);
		bar.setChannel(mainChannel);
		channelsAdapter.setColorMode(currentMode);
		spinnerChannel.setSelection(0);
	}
	
	private void setMainChannel(ColorChannel mainChannel)
	{
		squarePanel.setModeAndMainChannel(currentMode, mainChannel);
		bar.setChannel(mainChannel);
	}
}