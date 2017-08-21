package pl.karol202.paintplus;

import android.app.Fragment;
import android.os.Bundle;
import pl.karol202.paintplus.helpers.HelpersManager;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.Tools;

public class AppDataFragment extends Fragment
{
	public static final String TAG = "DATA_FRAGMENT";
	
	private AsyncManager asyncManager;
	private Image image;
	private Tools tools;
	private Tool tool;
	private HelpersManager helpersManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		image = new Image(getActivity());
		image.newImage(600, 600);
		
		tools = new Tools(image, asyncManager);
		tool = tools.getTool(1);
		
		helpersManager = new HelpersManager(image, getResources());
	}
	
	public void setAsyncManager(AsyncManager asyncManager)
	{
		this.asyncManager = asyncManager;
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public Tools getTools()
	{
		return tools;
	}
	
	public Tool getTool()
	{
		return tool;
	}
	
	public void setTool(Tool tool)
	{
		this.tool = tool;
	}
	
	public HelpersManager getHelpersManager()
	{
		return helpersManager;
	}
}