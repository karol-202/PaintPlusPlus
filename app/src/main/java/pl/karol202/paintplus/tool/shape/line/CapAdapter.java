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

package pl.karol202.paintplus.tool.shape.line;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pl.karol202.paintplus.R;

public class CapAdapter extends ArrayAdapter<Cap>
{
	CapAdapter(Context context)
	{
		super(context, R.layout.spinner_item_cap, Cap.values());
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent)
	{
		View view;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.spinner_item_cap, parent, false);
		}
		else view = convertView;
		Cap cap = getItem(position);

		ImageView imageView = view.findViewById(R.id.image_cap_icon);
		imageView.setImageResource(cap.getIcon());

		TextView textView = view.findViewById(R.id.text_cap_name);
		textView.setText(cap.getName());
		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent)
	{
		return getView(position, convertView, parent);
	}
}
