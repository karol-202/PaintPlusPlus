package pl.karol202.paintplus.tool.shape;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ShapeProperties extends Fragment
{
	private int shapeId;
	private Shapes shapes;
	protected Shape shape;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Fragment parent = getParentFragment();
		if(!(parent instanceof ShapeToolProperties))
			throw new RuntimeException("This fragment can only be attached to ShapeToolProperties.");
		ShapeToolProperties shapeProperties = (ShapeToolProperties) parent;
		shapes = shapeProperties.getShapes();
		if(shapes == null) throw new NullPointerException("Shapes object is null.");
		
		Bundle bundle = getArguments();
		if(bundle == null) throw new RuntimeException("No arguments found.");
		this.shapeId = bundle.getInt("shape");
		if(shapeId == -1) throw new RuntimeException("-1 is not valid shape id.");
		this.shape = shapes.getShape(shapeId);
		return null;
	}
}