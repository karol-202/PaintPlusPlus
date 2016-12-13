package pl.karol202.paintplus.tool.properties;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.shape.*;
import pl.karol202.paintplus.tool.shape.properties.ShapeProperties;

public class ShapeToolProperties extends ToolProperties implements OnItemSelectedListener, OnShapeEditListener
{
	private FragmentManager fragments;
	private ToolShape shapeTool;
	private Shapes shapes;
	private ShapeAdapter shapeAdapter;
	
	private View view;
	private Spinner spinnerShape;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_shape, container, false);
		setHasOptionsMenu(true);
		
		fragments = getChildFragmentManager();
		shapeTool = (ToolShape) tool;
		shapeTool.setShapeEditListener(this);
		shapes = shapeTool.getShapesClass();
		shapeAdapter = new ShapeAdapter(getActivity(), shapes.getShapes());
		
		spinnerShape = (Spinner) view.findViewById(R.id.spinner_shape);
		spinnerShape.setAdapter(shapeAdapter);
		spinnerShape.setSelection(getShapeId(shapeTool.getShape()));
		spinnerShape.setOnItemSelectedListener(this);
		
		tryToUpdateFragment();
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		if(shapeTool.isInEditMode()) inflater.inflate(R.menu.menu_tool_shape, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(shapeTool.isInEditMode())
		{
			int id = item.getItemId();
			switch(id)
			{
			case R.id.action_apply:
				shapeTool.apply();
			case R.id.action_cancel:
				shapeTool.cancel();
			}
			getActivity().invalidateOptionsMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		Shape shape = shapes.getShape(position);
		shapeTool.setShape(shape);
		tryToUpdateFragment();
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) { }
	
	@Override
	public void onStartShapeEditing()
	{
		getActivity().invalidateOptionsMenu();
	}
	
	private void tryToUpdateFragment()
	{
		try
		{
			updateFragment();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Error: Could not instantiate fragment from fragment class." +
									   "Probably the fragment class does not contain " +
									   "default constructor.", e);
		}
	}
	
	private void updateFragment() throws java.lang.InstantiationException, IllegalAccessException
	{
		Shape shape = shapeTool.getShape();
		Class<? extends ShapeProperties> propertiesClass = shape.getPropertiesClass();
		ShapeProperties properties = propertiesClass.newInstance();
		
		Bundle params = new Bundle();
		params.putInt("shape", getShapeId(shape));
		properties.setArguments(params);
		
		FragmentTransaction transaction = fragments.beginTransaction();
		transaction.replace(R.id.fragment_shape, properties);
		transaction.commit();
	}
	
	private int getShapeId(Shape shape)
	{
		return shapes.getShapeId(shape);
	}
	
	public Shapes getShapes()
	{
		return shapes;
	}
}