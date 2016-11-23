package pl.karol202.paintplus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pl.karol202.paintplus.tool.ToolType;

public class AdapterListTools extends ArrayAdapter<ToolType>
{
	private Context context;

	public AdapterListTools(Context context, ToolType[] toolTypes)
	{
		super(context, R.layout.item_tool, toolTypes);
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
		ImageView imageItemTool = (ImageView) view.findViewById(R.id.image_item_tool);
		TextView textItemTool = (TextView) view.findViewById(R.id.text_item_tool);
		ToolType tool = getItem(position);
		imageItemTool.setImageResource(tool.getIcon());
		textItemTool.setText(tool.getName());
		return view;
	}
}
