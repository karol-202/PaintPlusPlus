package pl.karol202.paintplus.color.picker.numerical;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.picker.ColorPickerFragment;
import pl.karol202.paintplus.util.ColorPreviewView;

public class ColorNumericalFragment extends ColorPickerFragment
{
	private ColorPickerNumericalInterface pickerInterface;
	private ColorMode colorModeRGB;
	private ColorMode colorModeHSV;
	private ColorMode colorModeCMYK;
	
	private View view;
	private ColorPreviewView colorView;
	private View channelViewA;
	private View channelViewB;
	private View channelViewC;
	private View channelViewD;
	private View channelViewE;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.color_picker_numerical, container, false);
		
		pickerInterface = new ColorPickerNumericalInterface(ColorNumericalFragment.this);
		
		colorView = view.findViewById(R.id.view_colors_numerical_color);
		colorView.setColor(getCurrentColor());
		
		channelViewA = view.findViewById(R.id.colors_numerical_channel_a);
		channelViewB = view.findViewById(R.id.colors_numerical_channel_b);
		channelViewC = view.findViewById(R.id.colors_numerical_channel_c);
		channelViewD = view.findViewById(R.id.colors_numerical_channel_d);
		channelViewE = view.findViewById(R.id.colors_numerical_channel_e);
		
		colorModeRGB = new ColorModeRGB(pickerInterface);
		colorModeHSV = new ColorModeHSV(pickerInterface);
		colorModeCMYK = new ColorModeCMYK(pickerInterface);
		colorModeRGB.updateChannels();
		
		return view;
	}
	
	@Override
	protected boolean onColorModeSelected(int actionId)
	{
		switch(actionId)
		{
		case R.id.mode_rgb: colorModeRGB.updateChannels(); break;
		case R.id.mode_hsv: colorModeHSV.updateChannels(); break;
		case R.id.mode_cmyk: colorModeCMYK.updateChannels(); break;
		default: return false;
		}
		return true;
	}
	
	@Override
	protected boolean isColorModeSupported(int actionId)
	{
		return actionId == R.id.mode_rgb ||
			   actionId == R.id.mode_hsv ||
			   actionId == R.id.mode_cmyk;
	}
	
	void updateColor(int color)
	{
		setCurrentColor(color);
		colorView.setColor(color);
	}
	
	View getChannelViewA()
	{
		return channelViewA;
	}
	
	View getChannelViewB()
	{
		return channelViewB;
	}
	
	View getChannelViewC()
	{
		return channelViewC;
	}
	
	View getChannelViewD()
	{
		return channelViewD;
	}
	
	View getChannelViewE()
	{
		return channelViewE;
	}
}