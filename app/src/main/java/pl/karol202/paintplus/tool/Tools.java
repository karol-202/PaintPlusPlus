package pl.karol202.paintplus.tool;

import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.brush.ToolBrush;
import pl.karol202.paintplus.tool.drag.ToolDrag;
import pl.karol202.paintplus.tool.fill.ToolFill;
import pl.karol202.paintplus.tool.gradient.ToolGradient;
import pl.karol202.paintplus.tool.marker.ToolMarker;
import pl.karol202.paintplus.tool.pan.ToolPan;
import pl.karol202.paintplus.tool.pickcolor.ToolColorPick;
import pl.karol202.paintplus.tool.rubber.ToolRubber;
import pl.karol202.paintplus.tool.selection.ToolSelection;
import pl.karol202.paintplus.tool.shape.ToolShape;

import java.util.ArrayList;

public class Tools
{
	private ArrayList<Tool> tools;
	private Tool tool;
	
	public Tools(Image image, AsyncManager asyncManager)
	{
		tools = new ArrayList<>();
		tools.add(new ToolPan(image));
		tools.add(new ToolMarker(image));
		tools.add(new ToolBrush(image));
		tools.add(new ToolFill(image, asyncManager));
		tools.add(new ToolShape(image));
		tools.add(new ToolSelection(image));
		tools.add(new ToolColorPick(image));
		tools.add(new ToolDrag(image));
		tools.add(new ToolRubber(image));
		tools.add(new ToolGradient(image));
		
		tool = tools.get(0);
	}
	
	Tool getTool(int id)
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
	
	int getToolsAmount()
	{
		return tools.size();
	}
	
	public Tool getCurrentTool()
	{
		return tool;
	}
	
	public void setCurrentTool(Tool tool)
	{
		this.tool = tool;
	}
}