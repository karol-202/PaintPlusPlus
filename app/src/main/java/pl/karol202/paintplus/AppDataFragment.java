package pl.karol202.paintplus;

import android.app.Fragment;
import android.os.Bundle;
import pl.karol202.paintplus.Image.OnImageChangeListener;
import pl.karol202.paintplus.tool.Tools;

public class AppDataFragment extends Fragment
{
	public static final String TAG = "DATA_FRAGMENT";
	
	private Image image;
	private Tools tools;
	private OnImageChangeListener listener;
	private AsyncBlocker asyncBlocker;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		image = new Image();
		image.createBitmap(600, 600);
		
		tools = new Tools(image, listener, asyncBlocker);
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public Tools getTools()
	{
		return tools;
	}
	
	public void setOnImageChangeListener(OnImageChangeListener listener)
	{
		this.listener = listener;
	}
	
	public void setAsyncBlocker(AsyncBlocker asyncBlocker)
	{
		this.asyncBlocker = asyncBlocker;
	}
}