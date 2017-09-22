package pl.karol202.paintplus.color.picker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.util.ColorPreviewView;

import java.util.HashMap;
import java.util.Map;

public class ColorNumericalFragment extends ColorPickerFragment
{
	private interface ColorFunction
	{
		int getChannelFromColor(int color);
	}
	
	private enum Channel
	{
		ALPHA(R.string.channel_a, 255, new ColorFunction() {
			@Override
			public int getChannelFromColor(int color)
			{
				return Color.alpha(color);
			}
		}),
		RED(R.string.channel_r, 255, new ColorFunction() {
			@Override
			public int getChannelFromColor(int color)
			{
				return Color.red(color);
			}
		}),
		GREEN(R.string.channel_g, 255, new ColorFunction() {
			@Override
			public int getChannelFromColor(int color)
			{
				return Color.green(color);
			}
		}),
		BLUE(R.string.channel_b, 255, new ColorFunction() {
			@Override
			public int getChannelFromColor(int color)
			{
				return Color.blue(color);
			}
		});
		
		private int name;
		private int maxValue;
		private ColorFunction colorFunction;
		
		Channel(int name, int maxValue, ColorFunction colorFunction)
		{
			this.name = name;
			this.maxValue = maxValue;
			this.colorFunction = colorFunction;
		}
		
		int getName()
		{
			return name;
		}
		
		int getMaxValue()
		{
			return maxValue;
		}
		
		int getChannelFromColor(int color)
		{
			return colorFunction.getChannelFromColor(color);
		}
	}
	
	private class ChannelViews
	{
		private class SeekBarListener implements SeekBar.OnSeekBarChangeListener
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				onColorChanged();
				textChannelValue.setText(String.valueOf(progress));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
		}
		
		private TextView textChannelName;
		private SeekBar seekBarChannelValue;
		private TextView textChannelValue;
		
		ChannelViews(View view, int textNameId, int seekBarId, int textValueId, Channel channel)
		{
			textChannelName = view.findViewById(textNameId);
			textChannelName.setText(channel.getName());
			
			seekBarChannelValue = view.findViewById(seekBarId);
			seekBarChannelValue.setMax(channel.getMaxValue());
			seekBarChannelValue.setProgress(channel.getChannelFromColor(getCurrentColor()));
			seekBarChannelValue.setOnSeekBarChangeListener(new SeekBarListener());
			
			textChannelValue = view.findViewById(textValueId);
			textChannelValue.setText(String.valueOf(channel.getChannelFromColor(getCurrentColor())));
		}
		
		void hide()
		{
			textChannelName.setVisibility(View.GONE);
			seekBarChannelValue.setVisibility(View.GONE);
			textChannelValue.setVisibility(View.GONE);
		}
		
		int getValue()
		{
			return seekBarChannelValue.getProgress();
		}
	}
	
	private View view;
	private ColorPreviewView colorView;
	private Map<Channel, ChannelViews> channels;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fragment_color_numerical, container, false);
		
		colorView = view.findViewById(R.id.view_colors_numerical_color);
		colorView.setColor(getCurrentColor());
		
		channels = new HashMap<>();
		initChannels();
		
		return view;
	}
	
	private void initChannels()
	{
		ChannelViews channelA = new ChannelViews(view, R.id.text_colors_numerical_channel_a,
													   R.id.seekBar_colors_numerical_channel_a,
													   R.id.text_colors_numerical_channel_a_value, Channel.ALPHA);
		ChannelViews channelB = new ChannelViews(view, R.id.text_colors_numerical_channel_b,
													   R.id.seekBar_colors_numerical_channel_b,
													   R.id.text_colors_numerical_channel_b_value, Channel.RED);
		ChannelViews channelC = new ChannelViews(view, R.id.text_colors_numerical_channel_c,
													   R.id.seekBar_colors_numerical_channel_c,
													   R.id.text_colors_numerical_channel_c_value, Channel.GREEN);
		ChannelViews channelD = new ChannelViews(view, R.id.text_colors_numerical_channel_d,
													   R.id.seekBar_colors_numerical_channel_d,
													   R.id.text_colors_numerical_channel_d_value, Channel.BLUE);
		
		if(isUsingAlpha()) channels.put(Channel.ALPHA, channelA);
		else channelA.hide();
		
		channels.put(Channel.RED, channelB);
		channels.put(Channel.GREEN, channelC);
		channels.put(Channel.BLUE, channelD);
	}
	
	private void onColorChanged()
	{
		int alpha = channels.containsKey(Channel.ALPHA) ? channels.get(Channel.ALPHA).getValue() : 255;
		int red = channels.get(Channel.RED).getValue();
		int green = channels.get(Channel.GREEN).getValue();
		int blue = channels.get(Channel.BLUE).getValue();
		int color = Color.argb(alpha, red, green, blue);
		
		setCurrentColor(color);
		colorView.setColor(color);
	}
}