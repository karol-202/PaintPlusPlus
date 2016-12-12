package pl.karol202.paintplus.tool.shape.properties.line;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pl.karol202.paintplus.R;

public class CapAdapter extends ArrayAdapter<Cap>
{
	protected CapAdapter(Context context)
	{
		super(context, R.layout.spinner_item_cap, Cap.values());
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.spinner_item_cap, parent, false);
		}
		else view = convertView;
		Cap cap = getItem(position);
		
		ImageView imageView = (ImageView) view.findViewById(R.id.image_cap_icon);
		imageView.setImageResource(cap.getIcon());
		
		TextView textView = (TextView) view.findViewById(R.id.text_cap_name);
		textView.setText(cap.getName());
		return view;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		return getView(position, convertView, parent);
	}
}