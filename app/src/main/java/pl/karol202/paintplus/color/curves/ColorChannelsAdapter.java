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

package pl.karol202.paintplus.color.curves;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.curves.ColorChannel.ColorChannelType;

public class ColorChannelsAdapter extends ArrayAdapter<ColorChannel>
{
	public ColorChannelsAdapter(Context context, ColorChannelType channelType)
	{
		super(context, R.layout.spinner_item_color_channel, ColorChannel.filterByType(channelType));
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent)
	{
		View view = convertView;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.spinner_item_color_channel, parent, false);
		}
		ColorChannel channel = getItem(position);

		ImageView imageView = view.findViewById(R.id.image_color_channel);
		imageView.setImageResource(channel.getIcon());

		TextView textView = view.findViewById(R.id.text_color_channel);
		textView.setText(channel.getDisplayName());
		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent)
	{
		return getView(position, convertView, parent);
	}
}
