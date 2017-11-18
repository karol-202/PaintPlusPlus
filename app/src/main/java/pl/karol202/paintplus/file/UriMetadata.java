package pl.karol202.paintplus.file;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import pl.karol202.paintplus.R;

public class UriMetadata
{
	private Context context;
	
	private String displayName;
	private String size;
	
	public UriMetadata(Context context, Uri uri)
	{
		this.context = context;
		readMetadata(context.getContentResolver().query(uri, null, null, null, null));
	}
	
	private void readMetadata(Cursor cursor)
	{
		if(cursor == null || !cursor.moveToFirst()) return;
		try
		{
			displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
			
			int columnSize = cursor.getColumnIndex(OpenableColumns.SIZE);
			if(cursor.isNull(columnSize)) size = context.getString(R.string.unknown);
			else size = cursor.getString(columnSize);
		}
		finally
		{
			cursor.close();
		}
	}
	
	public String getDisplayName()
	{
		return displayName;
	}
	
	public String getSize()
	{
		return size;
	}
}