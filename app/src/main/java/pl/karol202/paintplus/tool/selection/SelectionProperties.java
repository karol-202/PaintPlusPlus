package pl.karol202.paintplus.tool.selection;

import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.ToolProperties;

public class SelectionProperties extends ToolProperties implements OnItemSelectedListener, OnSelectionEditListener
{
	private ToolSelection selection;
	private SelectionShapeAdapter adapterShape;
	private SelectionModeAdapter adapterMode;
	
	private View view;
	private Spinner spinnerShape;
	private Spinner spinnerMode;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_selection, container, false);
		setHasOptionsMenu(true);
		
		selection = (ToolSelection) tool;
		selection.setSelectionListener(this);
		adapterShape = new SelectionShapeAdapter(getActivity());
		adapterMode = new SelectionModeAdapter(getActivity());
		
		spinnerShape = view.findViewById(R.id.spinner_selection_shape);
		spinnerShape.setAdapter(adapterShape);
		spinnerShape.setSelection(selection.getShape().ordinal());
		spinnerShape.setOnItemSelectedListener(this);
		
		spinnerMode = view.findViewById(R.id.spinner_selection_mode);
		spinnerMode.setAdapter(adapterMode);
		spinnerMode.setSelection(selection.getMode().ordinal());
		spinnerMode.setOnItemSelectedListener(this);
		
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
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		if(parent == spinnerShape) onShapeSelected(position);
		else if(parent == spinnerMode) onModeSelected(position);
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
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) { }
	
	@Override
	public void onStartSelectionEditing()
	{
		getActivity().invalidateOptionsMenu();
	}
}