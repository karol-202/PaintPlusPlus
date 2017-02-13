package pl.karol202.paintplus.color;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorChannel.ColorChannelType;

public class ColorChannelsAdapter extends ArrayAdapter<ColorChannel>
{
	public ColorChannelsAdapter(Context context, ColorChannelType channelType)
	{
		super(context, R.layout.spinner_item_color_channel, ColorChannel.filterByType(channelType));
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.spinner_item_color_channel, parent, false);
		}
		else view = convertView;
		ColorChannel channel = getItem(position);
		
		ImageView imageView = (ImageView) view.findViewById(R.id.image_color_channel);
		imageView.setImageResource(channel.getIcon());
		
		TextView textView = (TextView) view.findViewById(R.id.text_color_channel);
		textView.setText(channel.getName());
		return view;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		return getView(position, convertView, parent);
	}
}