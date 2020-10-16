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

package pl.karol202.paintplus.image.layer.mode;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import pl.karol202.paintplus.R;

public class LayerModeAdapter extends ArrayAdapter<LayerModeType>
{
	public LayerModeAdapter(Context context)
	{
		super(context, R.layout.spinner_item_layer_mode, LayerModeType.values());
	}
	
	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent)
	{
		return getItemView(position, convertView, parent, false);
	}
	
	@Override
	public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent)
	{
		return getItemView(position, convertView, parent, true);
	}
	
	private View getItemView(int position, View convertView, ViewGroup parent, boolean dropdown)
	{
		View view;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.spinner_item_layer_mode, parent, false);
		}
		else view = convertView;
		LayerModeType mode = getItem(position);
		
		int backgroundColor = ResourcesCompat.getColor(getContext().getResources(), R.color.layer_mode_item_background, null);
		if(dropdown) view.setBackgroundColor(backgroundColor);
		
		TextView textView = view.findViewById(R.id.text_layer_mode_name);
		textView.setText(mode.getName());
		
		View divider = view.findViewById(R.id.divider_layer_mode);
		divider.setVisibility(doesShowDivider(position, mode) && dropdown ? View.VISIBLE : View.GONE);
		return view;
	}
	
	private boolean doesShowDivider(int position, LayerModeType mode)
	{
		return position != getCount() - 1 && getItem(position + 1).getCategory() != mode.getCategory();
	}
}