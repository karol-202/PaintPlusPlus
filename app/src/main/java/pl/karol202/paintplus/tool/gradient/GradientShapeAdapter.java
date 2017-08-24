package pl.karol202.paintplus.tool.gradient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pl.karol202.paintplus.R;

import java.util.List;

public class GradientShapeAdapter extends ArrayAdapter<GradientShape>
{
	GradientShapeAdapter(Context context, List<GradientShape> shapes)
	{
		super(context, R.layout.spinner_item_gradient_shape, shapes);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.spinner_item_gradient_shape, parent, false);
		}
		else view = convertView;
		GradientShape shape = getItem(position);
		
		ImageView imageIcon = (ImageView) view.findViewById(R.id.image_gradient_shape_icon);
		imageIcon.setImageResource(shape.getIcon());
		
		TextView textName = (TextView) view.findViewById(R.id.text_gradient_shape_name);
		textName.setText(shape.getName());
		return view;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		return getView(position, convertView, parent);
	}
}