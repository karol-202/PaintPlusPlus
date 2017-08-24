package pl.karol202.paintplus.tool.gradient;

import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Spinner;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.tool.gradient.ToolGradient.OnGradientEditListener;

public class GradientProperties extends ToolProperties implements OnGradientEditListener, AdapterView.OnItemSelectedListener, View.OnClickListener
{
	private ToolGradient toolGradient;
	private GradientShapes shapes;
	private GradientShapeAdapter adapterGradientShape;
	
	private View view;
	private GradientPreviewView gradientPreview;
	private Spinner spinnerGradientShape;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		view = inflater.inflate(R.layout.properties_gradient, container, false);
		toolGradient = (ToolGradient) tool;
		toolGradient.setOnGradientEditListener(this);
		shapes = toolGradient.getShapes();
		adapterGradientShape = new GradientShapeAdapter(getActivity(), shapes.getShapes());
		
		gradientPreview = (GradientPreviewView) view.findViewById(R.id.gradient_preview);
		gradientPreview.setGradient(toolGradient.getGradient());
		gradientPreview.setOnClickListener(this);
		
		spinnerGradientShape = (Spinner) view.findViewById(R.id.spinner_gradient_shape);
		spinnerGradientShape.setAdapter(adapterGradientShape);
		spinnerGradientShape.setSelection(shapes.getIdOfShape(toolGradient.getShape()));
		spinnerGradientShape.setOnItemSelectedListener(this);
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		if(toolGradient.isInEditMode()) inflater.inflate(R.menu.menu_tool_gradient, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if(toolGradient.isInEditMode())
		{
			switch(id)
			{
			case R.id.action_apply:
				toolGradient.apply();
				break;
			case R.id.action_cancel:
				toolGradient.cancel();
				break;
			}
			getActivity().invalidateOptionsMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onGradientSet()
	{
		getActivity().invalidateOptionsMenu();
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		GradientShape shape = shapes.getShape(position);
		toolGradient.setShape(shape);
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		System.err.println("nothing!");
	}
	
	@Override
	public void onClick(View v)
	{
	
	}
}