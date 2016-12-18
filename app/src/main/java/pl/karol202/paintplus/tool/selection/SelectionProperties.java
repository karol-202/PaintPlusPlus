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
	private SelectionModeAdapter adapter;
	
	private View view;
	private Spinner spinner;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_selection, container, false);
		setHasOptionsMenu(true);
		
		selection = (ToolSelection) tool;
		selection.setSelectionListener(this);
		adapter = new SelectionModeAdapter(getActivity());
		
		spinner = (Spinner) view.findViewById(R.id.spinner_selection_mode);
		spinner.setAdapter(adapter);
		spinner.setSelection(selection.getMode().ordinal());
		spinner.setOnItemSelectedListener(this);
		
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
		if(selection.isInEditMode())
		{
			int id = item.getItemId();
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