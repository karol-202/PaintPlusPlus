package pl.karol202.paintplus.recent;

import android.content.Context;

public class RecentImageCreator implements OnFileEditListener
{
	private Context context;
	
	public RecentImageCreator(Context context)
	{
		this.context = context;
	}
	
	@Override
	public void onFileEdited(String path)
	{
		RecentLoader loader = new RecentLoader(context);
		loader.load();
		loader.addOrUpdateRecentImage(createRecentImage(path));
		loader.save();
	}
	
	private RecentImage createRecentImage(String path)
	{
		String[] parts = path.split("/");
		String name = parts[parts.length - 1];
		
		return new RecentImage(path, name, System.currentTimeMillis());
	}
}