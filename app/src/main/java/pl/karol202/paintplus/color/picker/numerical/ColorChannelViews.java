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

package pl.karol202.paintplus.color.picker.numerical;

import android.graphics.PorterDuff;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import pl.karol202.paintplus.R;

class ColorChannelViews
{
	private class SeekBarListener implements SeekBar.OnSeekBarChangeListener
	{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		{
			channel.setValue(progress);
			textChannelValue.setText(String.valueOf(progress));
			if(colorChangeListener != null) colorChangeListener.onColorChanged();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) { }

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) { }
	}

	private ColorChannel channel;
	private OnColorChangeListener colorChangeListener;

	private View channelView;
	private TextView textChannelName;
	private SeekBar seekBarChannelValue;
	private TextView textChannelValue;

	// Constructor for hiding channelView
	ColorChannelViews(View channelView)
	{
		this.channelView = channelView;
		channelView.setVisibility(View.GONE);
	}

	ColorChannelViews(View channelView, ColorChannel channel, OnColorChangeListener colorChangeListener)
	{
		this.channel = channel;
		this.colorChangeListener = colorChangeListener;
		this.channelView = channelView;

		textChannelName = channelView.findViewById(R.id.text_colors_numerical_channel);
		seekBarChannelValue = channelView.findViewById(R.id.seekBar_colors_numerical_channel);
		textChannelValue = channelView.findViewById(R.id.text_colors_numerical_channel_value);
	}

	void update()
	{
		channelView.setVisibility(channel != null && channel.isActive() ? View.VISIBLE : View.GONE);
		if(channel == null) return;

		textChannelName.setText(channel.getName());

		seekBarChannelValue.getProgressDrawable().setColorFilter(getSeekBarColor(), PorterDuff.Mode.SRC_ATOP);
		seekBarChannelValue.getThumb().setColorFilter(getSeekBarColor(), PorterDuff.Mode.SRC_ATOP);
		seekBarChannelValue.setOnSeekBarChangeListener(null);
		seekBarChannelValue.setMax(channel.getMaxValue());
		seekBarChannelValue.setProgress(channel.getValue());
		seekBarChannelValue.setOnSeekBarChangeListener(new SeekBarListener());

		textChannelValue.setText(String.valueOf(channel.getValue()));
	}

	private int getSeekBarColor()
	{
		return ResourcesCompat.getColor(channelView.getResources(), channel.getSeekBarColorId(), null);
	}
}
