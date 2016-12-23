package pl.karol202.paintplus;

import android.app.Fragment;
import android.os.Bundle;
import pl.karol202.paintplus.Image.OnImageChangeListener;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.Tools;

public class AppDataFragment extends Fragment
{
	public static final String TAG = "DATA_FRAGMENT";
	
	private OnImageChangeListener listener;
	private AsyncManager asyncManager;
	private Image image;
	private Tools tools;
	private Tool tool;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		image = new Image();
		image.createBitmap(600, 600);
		
		tools = new Tools(image, listener, asyncManager);
		
		tool = tools.getTool(1);
	}
	
	public void setOnImageChangeListener(OnImageChangeListener listener)
	{
		this.listener = listener;
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
}