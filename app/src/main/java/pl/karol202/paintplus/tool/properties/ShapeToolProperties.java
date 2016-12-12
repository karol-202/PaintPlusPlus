package pl.karol202.paintplus.tool.properties;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.shape.Shape;
import pl.karol202.paintplus.tool.shape.ShapeAdapter;
import pl.karol202.paintplus.tool.shape.Shapes;
import pl.karol202.paintplus.tool.shape.ToolShape;
import pl.karol202.paintplus.tool.shape.properties.ShapeProperties;

public class ShapeToolProperties extends ToolProperties implements AdapterView.OnItemSelectedListener
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
		
		fragments = getChildFragmentManager();
		shapeTool = (ToolShape) tool;
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
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		Shape shape = shapes.getShape(position);
		shapeTool.setShape(shape);
		tryToUpdateFragment();
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) { }
	
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