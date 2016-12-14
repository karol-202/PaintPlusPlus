package pl.karol202.paintplus.tool.shape;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.karol202.paintplus.tool.properties.ShapeToolProperties;

public class ShapeProperties extends Fragment
{
	private int shapeId;
	private Shapes shapes;
	protected Shape shape;
	
	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		Fragment parent = getParentFragment();
		if(!(parent instanceof ShapeToolProperties))
			throw new RuntimeException("This fragment can only be attached to ShapeToolProperties.");
		ShapeToolProperties shapeProperties = (ShapeToolProperties) parent;
		shapes = shapeProperties.getShapes();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Bundle bundle = getArguments();
		if(bundle == null) throw new RuntimeException("No arguments found.");
		this.shapeId = bundle.getInt("shape");
		if(shapeId == -1) throw new RuntimeException("-1 is not valid shape id.");
		this.shape = shapes.getShape(shapeId);
		return null;
	}
}