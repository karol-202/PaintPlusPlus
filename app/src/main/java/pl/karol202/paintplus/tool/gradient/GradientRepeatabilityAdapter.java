package pl.karol202.paintplus.tool.gradient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pl.karol202.paintplus.R;

public class GradientRepeatabilityAdapter extends ArrayAdapter<GradientRepeatability>
{
	GradientRepeatabilityAdapter(Context context)
	{
		super(context, R.layout.spinner_item_gradient_repeatability, GradientRepeatability.values());
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.spinner_item_gradient_repeatability, parent, false);
		}
		else view = convertView;
		GradientRepeatability shape = getItem(position);
		
		ImageView imageIcon = (ImageView) view.findViewById(R.id.image_gradient_repeatability_icon);
		imageIcon.setImageResource(shape.getIcon());
		
		TextView textName = (TextView) view.findViewById(R.id.text_gradient_repeatability_name);
		textName.setText(shape.getName());
		return view;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		return getView(position, convertView, parent);
	}
}