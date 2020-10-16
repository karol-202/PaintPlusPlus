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

package pl.karol202.paintplus.tool.drag;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.ToolProperties;

public class DragProperties extends ToolProperties
{
	private ToolDrag drag;
	
	private View view;
	private CheckBox checkOneAxis;
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_drag, container, false);
		drag = (ToolDrag) tool;
		
		checkOneAxis = view.findViewById(R.id.check_drag_one_axis);
		checkOneAxis.setChecked(drag.isOneAxis());
		checkOneAxis.setOnCheckedChangeListener((buttonView, isChecked) -> drag.setOneAxis(isChecked));
		
		return view;
	}
}