package pl.karol202.paintplus.tool.selection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pl.karol202.paintplus.R;

public class SelectionShapeAdapter extends ArrayAdapter<ToolSelectionShape>
{
	public SelectionShapeAdapter(Context context)
	{
		super(context, R.layout.spinner_item_selection_shape, ToolSelectionShape.values());
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.spinner_item_selection_shape, parent, false);
		}
		else view = convertView;
		ToolSelectionShape mode = getItem(position);
		
		ImageView imageView = (ImageView) view.findViewById(R.id.image_selection_shape_icon);
		imageView.setImageResource(mode.getIcon());
		
		TextView textView = (TextView) view.findViewById(R.id.text_selection_shape_name);
		textView.setText(mode.getName());
		return view;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		return getView(position, convertView, parent);
	}
}