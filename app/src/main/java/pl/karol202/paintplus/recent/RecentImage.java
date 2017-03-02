package pl.karol202.paintplus.recent;

import android.graphics.Bitmap;

class RecentImage implements Comparable<RecentImage>
{
	private String path;
	private Bitmap thumbnail;
	private String name;
	private long date;
	
	RecentImage(String path, String name, long date)
	{
		this(path, null, name, date);
	}
	
	RecentImage(String path, Bitmap thumbnail, String name, long date)
	{
		this.path = path;
		this.thumbnail = thumbnail;
		this.name = name;
		this.date = date;
	}
	
	@Override
	public int compareTo(RecentImage o)
	{
		if(date == o.date) return 0;
		return date > o.date ? 1 : -1;
	}
	
	String getPath()
	{
		return path;
	}
	
	Bitmap getThumbnail()
	{
		return thumbnail;
	}
	
	String getName()
	{
		return name;
	}
	
	long getDate()
	{
		return date;
	}
}