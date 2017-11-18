package pl.karol202.paintplus.recent;

import android.graphics.Bitmap;
import android.net.Uri;

class RecentImage implements Comparable<RecentImage>
{
	private Uri uri;
	private Uri thumbnailUri;
	private Bitmap thumbnail;
	private String name;
	private long date;
	
	RecentImage(Uri uri, Uri thumbnailUri, Bitmap thumbnail, String name, long date)
	{
		this.uri = uri;
		this.thumbnailUri = thumbnailUri;
		this.thumbnail = thumbnail;
		this.name = name;
		this.date = date;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		RecentImage that = (RecentImage) o;
		
		if(uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
		return name != null ? name.equals(that.name) : that.name == null;
	}
	
	@Override
	public int hashCode()
	{
		int result = uri != null ? uri.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}
	
	@Override
	public int compareTo(RecentImage o)
	{
		if(date == o.date) return 0;
		return date < o.date ? 1 : -1;
	}
	
	Uri getUri()
	{
		return uri;
	}
	
	Uri getThumbnailUri()
	{
		return thumbnailUri;
	}
	
	void setThumbnailUri(Uri thumbnailUri)
	{
		this.thumbnailUri = thumbnailUri;
	}
	
	Bitmap getThumbnail()
	{
		return thumbnail;
	}
	
	void setThumbnail(Bitmap thumbnail)
	{
		this.thumbnail = thumbnail;
	}
	
	String getName()
	{
		return name;
	}
	
	long getDate()
	{
		return date;
	}
	
	void setDate(long date)
	{
		this.date = date;
	}
}