/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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