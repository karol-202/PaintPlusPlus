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

package pl.karol202.paintplus.file;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.R;

public interface BitmapSaveFormat
{
	class JPEGSaveFormat implements BitmapSaveFormat
	{
		private static final int DEFAULT_QUALITY = 95;
		
		private int quality;
		
		private SeekBar seekBarQuality;
		private TextView textViewQuality;
		
		JPEGSaveFormat()
		{
			this.quality = DEFAULT_QUALITY;
		}
		
		public JPEGSaveFormat(int quality)
		{
			this.quality = quality;
		}
		
		@Override
		public boolean providesSettingsDialog()
		{
			return true;
		}
		
		@Override
		public int getSettingsDialogTitle()
		{
			return R.string.dialog_jpeg_save_settings;
		}
		
		@Override
		public int getSettingsDialogLayout()
		{
			return R.layout.dialog_format_jpeg;
		}
		
		@Override
		public void customizeSettingsDialog(View view)
		{
			seekBarQuality = view.findViewById(R.id.seekBar_format_jpeg_quality);
			seekBarQuality.setProgress(quality);
			seekBarQuality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
			{
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
				{
					onQualityChanged(progress);
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) { }
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) { }
			});
			
			textViewQuality = view.findViewById(R.id.text_format_jpeg_quality);
			updateText();
		}
		
		private void onQualityChanged(int quality)
		{
			this.quality = quality;
			updateText();
		}
		
		private void updateText()
		{
			textViewQuality.setText(String.valueOf(quality));
		}
		
		int getQuality()
		{
			return quality;
		}
	}
	
	class PNGSaveFormat implements BitmapSaveFormat
	{
		@Override
		public boolean providesSettingsDialog()
		{
			return false;
		}
		
		@Override
		public int getSettingsDialogTitle()
		{
			return 0;
		}
		
		@Override
		public int getSettingsDialogLayout()
		{
			return 0;
		}
		
		@Override
		public void customizeSettingsDialog(View view) { }
	}
	
	class WEBPSaveFormat implements BitmapSaveFormat
	{
		@Override
		public boolean providesSettingsDialog()
		{
			return false;
		}
		
		@Override
		public int getSettingsDialogTitle()
		{
			return 0;
		}
		
		@Override
		public int getSettingsDialogLayout()
		{
			return 0;
		}
		
		@Override
		public void customizeSettingsDialog(View view) { }
	}
	
	class BMPSaveFormat implements BitmapSaveFormat
	{
		@Override
		public boolean providesSettingsDialog()
		{
			return false;
		}
		
		@Override
		public int getSettingsDialogTitle()
		{
			return 0;
		}
		
		@Override
		public int getSettingsDialogLayout()
		{
			return 0;
		}
		
		@Override
		public void customizeSettingsDialog(View view) { }
	}
	
	class GIFSaveFormat implements BitmapSaveFormat
	{
		private static final boolean DEFAULT_DITHERING = false;
		
		private boolean dithering;
		
		private CheckBox checkBoxDithering;
		
		GIFSaveFormat()
		{
			dithering = DEFAULT_DITHERING;
		}
		
		@Override
		public boolean providesSettingsDialog()
		{
			return true;
		}
		
		@Override
		public int getSettingsDialogTitle()
		{
			return R.string.dialog_gif_save_settings;
		}
		
		@Override
		public int getSettingsDialogLayout()
		{
			return R.layout.dialog_format_gif;
		}
		
		@Override
		public void customizeSettingsDialog(View view)
		{
			checkBoxDithering = view.findViewById(R.id.check_format_gif_dithering);
			checkBoxDithering.setChecked(dithering);
			checkBoxDithering.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					GIFSaveFormat.this.dithering = isChecked;
				}
			});
		}
		
		boolean getDithering()
		{
			return dithering;
		}
	}
	
	boolean providesSettingsDialog();
	
	int getSettingsDialogTitle();
	
	int getSettingsDialogLayout();
	
	void customizeSettingsDialog(View view);
}