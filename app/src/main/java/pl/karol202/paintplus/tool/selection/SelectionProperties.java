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

package pl.karol202.paintplus.tool.selection;

import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.ToolProperties;

public class SelectionProperties extends ToolProperties
{
	private ToolSelection selection;
	private SelectionShapeAdapter adapterShape;
	private SelectionModeAdapter adapterMode;
	
	private View view;
	private Spinner spinnerShape;
	private Spinner spinnerMode;
	private Button buttonSelectAll;
	private Button buttonSelectNothing;
	private Button buttonInvertSelection;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_selection, container, false);
		setHasOptionsMenu(true);
		
		selection = (ToolSelection) tool;
		selection.setSelectionListener(new OnSelectionEditListener() {
			@Override
			public void onStartSelectionEditing()
			{
				getActivity().invalidateOptionsMenu();
			}
		});
		adapterShape = new SelectionShapeAdapter(getActivity());
		adapterMode = new SelectionModeAdapter(getActivity());
		
		spinnerShape = view.findViewById(R.id.spinner_selection_shape);
		spinnerShape.setAdapter(adapterShape);
		spinnerShape.setSelection(selection.getShape().ordinal());
		spinnerShape.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				onShapeSelected(position);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			
			}
		});
		
		spinnerMode = view.findViewById(R.id.spinner_selection_mode);
		spinnerMode.setAdapter(adapterMode);
		spinnerMode.setSelection(selection.getMode().ordinal());
		spinnerMode.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				onModeSelected(position);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			
			}
		});
		
		buttonSelectAll = view.findViewById(R.id.button_selection_all);
		buttonSelectAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				selection.selectAll();
			}
		});
		
		buttonSelectNothing = view.findViewById(R.id.button_selection_nothing);
		buttonSelectNothing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				selection.selectNothing();
			}
		});
		
		buttonInvertSelection = view.findViewById(R.id.button_selection_invert);
		buttonInvertSelection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				selection.invertSelection();
			}
		});
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		if(selection.isInEditMode()) inflater.inflate(R.menu.menu_tool_selection, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if(selection.isInEditMode())
		{
			switch(id)
			{
			case R.id.action_apply:
				selection.applySelection();
				break;
			case R.id.action_cancel:
				selection.cancelSelection();
				break;
			}
			getActivity().invalidateOptionsMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void onShapeSelected(int position)
	{
		ToolSelectionShape shape = ToolSelectionShape.values()[position];
		selection.setShape(shape);
	}
	
	private void onModeSelected(int position)
	{
		ToolSelectionMode mode = ToolSelectionMode.values()[position];
		selection.setMode(mode);
	}
}