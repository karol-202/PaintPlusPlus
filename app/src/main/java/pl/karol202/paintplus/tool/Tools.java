package pl.karol202.paintplus.tool;

import pl.karol202.paintplus.Image;

import java.util.ArrayList;

public class Tools
{
	private ArrayList<Tool> tools;
	
	public Tools(Image image)
	{
		tools = new ArrayList<>();
		tools.add(new ToolPan(image));
		tools.add(new ToolMarker(image));
		tools.add(new ToolBrush(image));
	}
	
	public Tool getTool(int id)
	{
		return tools.get(id);
	}
	
	public int getToolId(Tool tool)
	{
		for(int i = 0; i < tools.size(); i++)
		{
			Tool next = tools.get(i);
			if(next == tool) return i;
		}
		return -1;
	}
	
	public ArrayList<Tool> getTools()
	{
		return tools;
	}
}