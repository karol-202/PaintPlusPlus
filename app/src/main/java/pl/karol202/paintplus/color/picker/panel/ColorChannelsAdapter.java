package pl.karol202.paintplus.color.picker.panel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.picker.panel.ColorChannel.ColorChannelType;

public class ColorChannelsAdapter extends BaseAdapter
{
	private Context context;
	private ColorMode colorMode;
	
	ColorChannelsAdapter(Context context)
	{
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.spinner_item_color_picker_channel, parent, false);
		}
		else view = convertView;
		ColorChannelType channel = getItem(position).getType();
		
		TextView textName = view.findViewById(R.id.text_channel_name);
		textName.setText(channel.getName());
		return view;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		return getView(position, convertView, parent);
	}
	
	@Override
	public int getCount()
	{
		return colorMode == null ? 0 : colorMode.getChannels().length;
	}
	
	@Override
	public ColorChannel getItem(int position)
	{
		if(colorMode == null) return null;
		return colorMode.getChannels()[position];
	}
	
	@Override
	public long getItemId(int position)
	{
		return 0;
	}
	
	void setColorMode(ColorMode mode)
	{
		this.colorMode = mode;
		notifyDataSetChanged();
	}
}