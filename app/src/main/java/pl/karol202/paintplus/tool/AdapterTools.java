package pl.karol202.paintplus.tool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pl.karol202.paintplus.R;

public class AdapterTools extends ArrayAdapter<Tool>
{
	private Context context;

	public AdapterTools(Context context, Tools tools)
	{
		super(context, R.layout.item_tool, tools.getTools());
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_tool, parent, false);
		}
		else view = convertView;
		Tool tool = getItem(position);
		
		ImageView imageItemTool = (ImageView) view.findViewById(R.id.image_item_tool);
		imageItemTool.setImageResource(tool.getIcon());
		
		TextView textItemTool = (TextView) view.findViewById(R.id.text_item_tool);
		textItemTool.setText(tool.getName());
		return view;
	}
}
