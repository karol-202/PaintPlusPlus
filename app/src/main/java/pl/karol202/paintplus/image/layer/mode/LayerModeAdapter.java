package pl.karol202.paintplus.image.layer.mode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import pl.karol202.paintplus.R;

public class LayerModeAdapter extends ArrayAdapter<LayerModeType>
{
	public LayerModeAdapter(Context context)
	{
		super(context, R.layout.spinner_item_layer_mode, LayerModeType.values());
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.spinner_item_layer_mode, parent, false);
		}
		else view = convertView;
		LayerModeType mode = getItem(position);
		
		TextView textView = (TextView) view.findViewById(R.id.text_layer_mode_name);
		textView.setText(mode.getName());
		return view;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		return getView(position, convertView, parent);
	}
}