package pl.karol202.paintplus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.Tools;

public class AppDataFragment extends Fragment
{
	public static final String TAG = "DATA_FRAGMENT";
	
	private AsyncManager asyncManager;
	private Image image;
	private Tools tools;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		image = new Image(getActivity());
		image.newImage(600, 600);
		
		tools = new Tools(image, asyncManager);
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
	
	public Tool getCurrentTool()
	{
		return tools.getCurrentTool();
	}

	public void setCurrentTool(Tool tool)
	{
		tools.setCurrentTool(tool);
	}
}